import os
import json
import sys
import threading
from typing import List, Optional, Dict, Any
from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib.parse import urlparse, parse_qs
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
	return query_books_data(
		query=query,
		author=author,
		isbn=isbn,
		status=status,
		priceMin=priceMin,
		priceMax=priceMax,
		limit=limit,
	)


def query_books_data(
	query: Optional[str] = None,
	author: Optional[str] = None,
	isbn: Optional[str] = None,
	status: Optional[str] = None,
	priceMin: Optional[float] = None,
	priceMax: Optional[float] = None,
	limit: Optional[int] = None,
) -> Dict[str, Any]:
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


# HTTP Server for n8n and other HTTP clients
class BookSearchHTTPHandler(BaseHTTPRequestHandler):
	def do_GET(self):
		"""Handle GET requests to /books endpoint"""
		parsed_path = urlparse(self.path)
		
		if parsed_path.path == "/books":
			# Parse query parameters
			query_params = parse_qs(parsed_path.query)
			
			# Extract parameters
			query = query_params.get("query", [None])[0]
			author = query_params.get("author", [None])[0]
			isbn = query_params.get("isbn", [None])[0]
			status = query_params.get("status", [None])[0]
			price_min = query_params.get("priceMin", [None])[0]
			price_max = query_params.get("priceMax", [None])[0]
			limit = query_params.get("limit", [None])[0]
			
			# Convert types
			price_min = float(price_min) if price_min else None
			price_max = float(price_max) if price_max else None
			limit = int(limit) if limit else None
			
			try:
				# Call the search_books function
				result = query_books_data(
					query=query,
					author=author,
					isbn=isbn,
					status=status,
					priceMin=price_min,
					priceMax=price_max,
					limit=limit,
				)
				
				# Send response
				self.send_response(200)
				self.send_header("Content-Type", "application/json")
				self.send_header("Access-Control-Allow-Origin", "*")
				self.send_header("Access-Control-Allow-Methods", "GET, OPTIONS")
				self.send_header("Access-Control-Allow-Headers", "Content-Type")
				self.end_headers()
				self.wfile.write(json.dumps(result, ensure_ascii=False).encode("utf-8"))
			except Exception as e:
				self.send_response(500)
				self.send_header("Content-Type", "application/json")
				self.send_header("Access-Control-Allow-Origin", "*")
				self.end_headers()
				error_response = {"error": str(e)}
				self.wfile.write(json.dumps(error_response).encode("utf-8"))
		
		elif parsed_path.path == "/health":
			# Health check endpoint
			try:
				with get_connection() as conn:
					with conn.cursor() as cur:
						cur.execute("SELECT 1")
				status = {"status": "healthy", "database": "connected"}
			except Exception as e:
				status = {"status": "unhealthy", "error": str(e)}
			
			self.send_response(200)
			self.send_header("Content-Type", "application/json")
			self.send_header("Access-Control-Allow-Origin", "*")
			self.end_headers()
			self.wfile.write(json.dumps(status).encode("utf-8"))
		
		else:
			self.send_response(404)
			self.send_header("Content-Type", "application/json")
			self.end_headers()
			self.wfile.write(json.dumps({"error": "Not found"}).encode("utf-8"))
	
	def do_OPTIONS(self):
		"""Handle OPTIONS requests for CORS preflight"""
		self.send_response(200)
		self.send_header("Access-Control-Allow-Origin", "*")
		self.send_header("Access-Control-Allow-Methods", "GET, OPTIONS")
		self.send_header("Access-Control-Allow-Headers", "Content-Type")
		self.end_headers()
	
	def log_message(self, format, *args):
		# Suppress default logging
		pass


def run_http_server(port=8787):
	"""Run HTTP server in a separate thread"""
	server = HTTPServer(("0.0.0.0", port), BookSearchHTTPHandler)
	print(f"HTTP server running on http://0.0.0.0:{port}")
	print(f"  - Books API: http://localhost:{port}/books")
	print(f"  - Health check: http://localhost:{port}/health")
	server.serve_forever()


if __name__ == "__main__":
	# Check if HTTP mode is requested
	http_mode = os.getenv("HTTP_MODE", "false").lower() == "true"
	http_port = int(os.getenv("HTTP_PORT", "8787"))
	
	if http_mode:
		# HTTP mode: run HTTP server only
		print("Running in HTTP mode...")
		run_http_server(http_port)
	else:
		# Check if both modes are requested
		enable_http = os.getenv("ENABLE_HTTP", "false").lower() == "true"
		
		if enable_http:
			# Start HTTP server in background thread
			http_thread = threading.Thread(target=run_http_server, args=(http_port,), daemon=True)
			http_thread.start()
			print("HTTP server started in background thread")
		
		# Run over stdio so Cherry Studio / MCP clients can connect
		print("Running in stdio mode (MCP protocol)...")
		app.run()


