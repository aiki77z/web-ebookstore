-- Create new table for book inventory and migrate data from books.stock

START TRANSACTION;

-- 1) Create inventory table
CREATE TABLE IF NOT EXISTS book_inventory (
  book_id BIGINT PRIMARY KEY,
  stock INT NOT NULL DEFAULT 0,
  updated_at DATETIME NULL,
  CONSTRAINT fk_book_inventory_book
    FOREIGN KEY (book_id) REFERENCES books(id)
    ON DELETE CASCADE
);

-- 2) Backfill data from books.stock
INSERT INTO book_inventory (book_id, stock, updated_at)
SELECT id AS book_id, COALESCE(stock, 0) AS stock, NOW()
FROM books
ON DUPLICATE KEY UPDATE stock = VALUES(stock), updated_at = VALUES(updated_at);

-- 3) Optional: initialize inventory row for books without stock
INSERT INTO book_inventory (book_id, stock, updated_at)
SELECT b.id, 0, NOW()
FROM books b
LEFT JOIN book_inventory bi ON bi.book_id = b.id
WHERE bi.book_id IS NULL;

COMMIT;

-- Optional (later): drop books.stock after application refactor is fully deployed
-- ALTER TABLE books DROP COLUMN stock;



