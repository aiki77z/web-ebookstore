## E-BookStore FastMCP Server (Python)

使用 FastMCP（Python）实现的 MCP 服务器，提供 `search_books` 工具从 MySQL `ebookstore` 数据库查询书籍。

### 环境准备
- Python 3.10+
- MySQL 可访问（数据库名 `ebookstore`）

### 安装

```bash
cd tools/mcp-book-search-fastpy
python -m venv .venv
.\.venv\Scripts\activate  # Windows PowerShell
pip install -r requirements.txt
```

### 配置环境变量
复制 `env.example` 为 `.env` 并填写数据库凭证：

```bash
copy env.example .env  # Windows PowerShell 可以使用 Copy-Item
```

变量说明：
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `QUERY_DEFAULT_LIMIT`（默认 20）
- `QUERY_MAX_LIMIT`（默认 100）

你的后端默认数据库配置可参考：

```1:8:backend/src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/ebookstore?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=zyqzyq041230
```

### 启动（被 MCP 客户端调用）

```bash
python server.py
```

进程将通过 stdio 与 MCP 客户端通信（Cherry Studio / Claude Desktop / Cursor MCP 等）。

### 在 Cherry Studio 中配置
1. 打开 Settings → MCP Servers → Add
2. Name: `EBookStore Search (FastMCP)`
3. Command: `D:\web\newebookstore\hw7\web-ebookstore\tools\mcp-book-search-fastpy\.venv\Scripts\python.exe`
4. Args: `D:\web\newebookstore\hw7\web-ebookstore\tools\mcp-book-search-fastpy\server.py`
5. Working Directory: 指向 `tools/mcp-book-search-fastpy`
6. Env: 读取 `.env`（在界面中填入逐行填入`.env`内容）
7. 保存并启用

### 工具：`search_books`
- 入参：
  - `query?`（匹配 `title` / `author` / `isbn`，不区分大小写）
  - `author?`, `isbn?`, `status?`
  - `priceMin?`, `priceMax?`
  - `limit?`（默认 `QUERY_DEFAULT_LIMIT`，上限 `QUERY_MAX_LIMIT`）
- 返回：`{ count, limit, results: Book[] }`，`Book` 字段包括：
  - `id, title, author, price, description, cover, status, stock, isbn, createdAt, updatedAt`
- 软删除：自动排除 `deleted = FALSE`

### 对比（建议截图）
- 未启用 MCP：提问“查找《三体》作者与价格”，模型可能无法返回权威数据
- 启用 FastMCP：调用 `search_books`，如 `{"query":"三体"}`，展示从数据库返回的结构化结果

### 故障排查
- 连接失败：确认 MySQL 运行、`.env` 凭证正确、库表存在
- 结果为空：确认 `books` 有数据且未被软删除


