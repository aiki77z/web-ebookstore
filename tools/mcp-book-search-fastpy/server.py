import os
import json
from typing import List, Optional, Dict, Any

import pymysql
from dotenv import load_dotenv

try:
	from fastmcp import FastMCP
except Exception as e:
	# Provide a clearer error if fastmcp isn't installed
	raise RuntimeError("fastmcp is required. Install with: pip install -r requirements.txt") from e


def getenv(name: str, default: Optional[str] = None) -> str:
	val = os.getenv(name, default)
	if val is None:
		raise RuntimeError(f"Missing required environment variable: {name}")
	return val


def getenv_int(name: str, default: int) -> int:
	raw = os.getenv(name)
	if raw is None:
		return default
	try:
		return int(raw)
	except ValueError:
		return default


load_dotenv()

APP_NAME = "ebookstore-book-search-fastmcp"
app = FastMCP(APP_NAME)


def get_connection():
	return pymysql.connect(
		host=getenv("DB_HOST", "localhost"),
		port=int(getenv("DB_PORT", "3306")),
		user=getenv("DB_USER", "root"),
		password=getenv("DB_PASSWORD", ""),
		database=getenv("DB_NAME", "ebookstore"),
		cursorclass=pymysql.cursors.DictCursor,
		charset="utf8mb4",
		autocommit=True,
	)


@app.tool()
def search_books(
	query: Optional[str] = None,
	author: Optional[str] = None,
	isbn: Optional[str] = None,
	status: Optional[str] = None,
	priceMin: Optional[float] = None,
	priceMax: Optional[float] = None,
	limit: Optional[int] = None,
) -> Dict[str, Any]:
	"""
	Search books from the E-BookStore MySQL database.

	Args:
		query: Free text to match title/author/isbn (case-insensitive).
		author: Filter by author (case-insensitive like match).
		isbn: Filter by exact ISBN.
		status: Filter by status, e.g. AVAILABLE, OUT_OF_STOCK.
		priceMin: Minimum price (inclusive).
		priceMax: Maximum price (inclusive).
		limit: Max number of results to return.

	Returns:
		A JSON-able dict: { "count": int, "limit": int, "results": [ ... ] }
	"""
	default_limit = getenv_int("QUERY_DEFAULT_LIMIT", 20)
	max_limit = getenv_int("QUERY_MAX_LIMIT", 100)
	resolved_limit = min(limit or default_limit, max_limit)

	where = ["(deleted IS NULL OR deleted = FALSE)"]
	params: List[Any] = []

	if query and query.strip():
		where.append("(LOWER(title) LIKE %s OR LOWER(author) LIKE %s OR LOWER(COALESCE(isbn, '')) LIKE %s)")
		like = f"%{query.strip().lower()}%"
		params.extend([like, like, like])

	if author and author.strip():
		where.append("LOWER(author) LIKE %s")
		params.append(f"%{author.strip().lower()}%")

	if isbn and isbn.strip():
		where.append("isbn = %s")
		params.append(isbn.strip())

	if status and status.strip():
		where.append("status = %s")
		params.append(status.strip())

	if priceMin is not None:
		where.append("price >= %s")
		params.append(priceMin)

	if priceMax is not None:
		where.append("price <= %s")
		params.append(priceMax)

	where_sql = f"WHERE {' AND '.join(where)}" if where else ""
	sql = f"""
		SELECT id, title, author, price, description, cover, status, stock, isbn, created_at, updated_at
		FROM books
		{where_sql}
		ORDER BY updated_at DESC, id DESC
		LIMIT %s
	"""
	params.append(resolved_limit)

	with get_connection() as conn:
		with conn.cursor() as cur:
			cur.execute(sql, params)
			rows = cur.fetchall()

	results: List[Dict[str, Any]] = []
	for r in rows:
		results.append(
			{
				"id": r.get("id"),
				"title": r.get("title"),
				"author": r.get("author"),
				"price": float(r["price"]) if r.get("price") is not None else None,
				"description": r.get("description"),
				"cover": r.get("cover"),
				"status": r.get("status"),
				"stock": r.get("stock"),
				"isbn": r.get("isbn"),
				"createdAt": r.get("created_at").isoformat() if r.get("created_at") else None,
				"updatedAt": r.get("updated_at").isoformat() if r.get("updated_at") else None,
			}
		)

	return {"count": len(results), "limit": resolved_limit, "results": results}


if __name__ == "__main__":
	# Run over stdio so Cherry Studio / MCP clients can connect
	app.run()


