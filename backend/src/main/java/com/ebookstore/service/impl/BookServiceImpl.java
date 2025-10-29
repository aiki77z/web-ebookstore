package com.ebookstore.service.impl;

import com.ebookstore.dto.BookDTO;
import com.ebookstore.dto.BookMetaDTO;
import com.ebookstore.entity.Book;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.repository.BookInventoryRepository;
import com.ebookstore.service.BookService;
import com.ebookstore.service.CartService;
import com.ebookstore.service.BookCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 书籍服务实现类
 * 使用Spring依赖注入
 */
@Service
public class BookServiceImpl implements BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private BookCacheService bookCacheService;
    
    @Autowired
    private BookInventoryRepository bookInventoryRepository;
    
    // 普通用户功能 - 只显示未删除的书籍
    
    @Override
    public List<BookDTO> getAllBooks() {
        System.out.println("[Service] getAllBooks called");
        // Try cache list of book meta (without stock), then fill stock from DB per item
        List<BookMetaDTO> cachedList = bookCacheService.getAllBookMetaList();
        if (cachedList != null) {
            long t0 = System.currentTimeMillis();
            List<BookDTO> rs = cachedList.stream()
                    .map(this::mergeMetaWithLiveStock)
                    .collect(Collectors.toList());
            long t1 = System.currentTimeMillis() - t0;
            System.out.println("[Service] getAllBooks from CACHE meta + DB stock, cost=" + t1 + "ms");
            return rs;
        }
        long t0 = System.currentTimeMillis();
        List<Book> books = bookRepository.findAllAvailable();
        List<BookDTO> result = books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        // store meta list to cache
        List<BookMetaDTO> metas = books.stream().map(this::toMeta).collect(Collectors.toList());
        bookCacheService.putAllBookMetaList(metas);
        long t1 = System.currentTimeMillis() - t0;
        System.out.println("[Service] getAllBooks from DB, cost=" + t1 + "ms");
        return result;
    }
    
    @Override
    public BookDTO getBookById(Long id) {
        System.out.println("[Service] getBookById id=" + id);
        // hit cache for meta
        BookMetaDTO meta = bookCacheService.getBookMeta(id);
        if (meta != null) {
            long t0 = System.currentTimeMillis();
            BookDTO dto = mergeMetaWithLiveStock(meta);
            long t1 = System.currentTimeMillis() - t0;
            System.out.println("[Service] getBookById from CACHE meta + DB stock, cost=" + t1 + "ms");
            return dto;
        }
        long t0 = System.currentTimeMillis();
        Book book = bookRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
        // backfill cache
        bookCacheService.putBookMeta(toMeta(book));
        BookDTO dto = convertToDTO(book);
        long t1 = System.currentTimeMillis() - t0;
        System.out.println("[Service] getBookById from DB, cost=" + t1 + "ms");
        return dto;
    }
    
    @Override
    public List<BookDTO> searchBooks(String query) {
        System.out.println("[Service] searchBooks query=" + query);
        // For search, we can fetch from DB and cache individual metas
        long t0 = System.currentTimeMillis();
        List<Book> books = bookRepository.searchAvailableBooks(query);
        List<BookDTO> list = books.stream()
                .map(book -> {
                    bookCacheService.putBookMeta(toMeta(book));
                    return convertToDTO(book);
                })
                .collect(Collectors.toList());
        long t1 = System.currentTimeMillis() - t0;
        System.out.println("[Service] searchBooks from DB, cost=" + t1 + "ms");
        return list;
    }
    
    // 管理员功能
    
    @Override
    public Book saveBook(Book book) {
        System.out.println("[Service] saveBook id=" + book.getId());
        Book saved = bookRepository.save(book);
        // ensure inventory row exists
        try {
            Integer existing = bookInventoryRepository.getStock(saved.getId());
            if (existing == null) {
                com.ebookstore.entity.BookInventory inv = new com.ebookstore.entity.BookInventory(
                        saved.getId(), 0, java.time.LocalDateTime.now());
                bookInventoryRepository.save(inv);
                System.out.println("[Service] inventory created for book id=" + saved.getId());
            }
        } catch (Exception e) {
            System.out.println("[Service] ensure inventory error: " + e.getMessage());
        }
        // evict caches
        bookCacheService.evictBook(saved.getId());
        return saved;
    }
    
    @Override
    @Transactional
    public boolean softDeleteBook(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
            
            // 软删除书籍
            book.setDeleted(true);
            bookRepository.save(book);
            // evict caches
            bookCacheService.evictBook(id);
            
            // 清理所有用户购物车中的该书籍
            int cleanedCount = cartService.cleanCartByBookId(id);
            System.out.println("书籍《" + book.getTitle() + "》已软删除，同时清理了 " + cleanedCount + " 个购物车项");
            
            return true;
        } catch (Exception e) {
            System.err.println("软删除书籍失败: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean restoreBook(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
            book.setDeleted(false);
            bookRepository.save(book);
            bookCacheService.evictBook(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public Page<Book> getAllBooksForAdmin(Pageable pageable) {
        return bookRepository.findAllForAdmin(pageable);
    }
    
    @Override
    public Book getBookEntityById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
    }
    
    @Override
    public Book getAvailableBookById(Long id) {
        return bookRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的书籍"));
    }
    
    @Override
    public List<Book> searchBooksForAdmin(String query) {
        return bookRepository.searchBooks(query);
    }
    
    // 库存管理方法 - 只能操作未删除的书籍
    
    @Override
    @Transactional
    public boolean updateStock(Long bookId, Integer quantity) {
        try {
            Book book = getAvailableBookById(bookId);
            // 更新库存表
            int updated = bookInventoryRepository.updateStock(bookId, quantity);
            if (updated == 0) {
                // 若不存在，则插入
                com.ebookstore.entity.BookInventory inv = new com.ebookstore.entity.BookInventory(bookId, quantity, java.time.LocalDateTime.now());
                bookInventoryRepository.save(inv);
            }
            // 根据库存量更新书籍状态
            book.setStatus(quantity > 0 ? "AVAILABLE" : "OUT_OF_STOCK");
            bookRepository.save(book);
            // evict caches so next read refreshes meta (status may change)
            bookCacheService.evictBook(bookId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean reduceStock(Long bookId, Integer quantity) {
        try {
            Book book = getAvailableBookById(bookId);
            Integer current = bookInventoryRepository.getStock(bookId);
            if (current == null) current = 0;
            if (current < quantity) {
                return false; // 库存不足
            }
            int newStock = current - quantity;
            bookInventoryRepository.updateStock(bookId, newStock);
            // 根据库存量更新状态
            if (newStock == 0) {
                book.setStatus("OUT_OF_STOCK");
                bookRepository.save(book);
            }
            // evict meta because status might change
            bookCacheService.evictBook(bookId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean checkStock(Long bookId, Integer quantity) {
        try {
            getAvailableBookById(bookId);
            Integer current = bookInventoryRepository.getStock(bookId);
            if (current == null) current = 0;
            return current >= quantity;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 统计和订单相关方法 - 保持历史数据完整性
    
    @Override
    public List<Book> getBooksByIds(List<Long> ids) {
        return bookRepository.findByIdIn(ids);
    }
    
    // 私有辅助方法
    
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPrice(book.getPrice());
        dto.setDescription(book.getDescription());
        dto.setCover(book.getCover());
        dto.setStatus(book.getStatus());
        Integer stock = bookInventoryRepository.getStock(book.getId());
        dto.setStock(stock == null ? 0 : stock);
        dto.setIsbn(book.getIsbn());
        dto.setDeleted(book.getDeleted());
        return dto;
    }

    private BookMetaDTO toMeta(Book book) {
        BookMetaDTO m = new BookMetaDTO();
        m.setId(book.getId());
        m.setTitle(book.getTitle());
        m.setAuthor(book.getAuthor());
        m.setPrice(book.getPrice());
        m.setDescription(book.getDescription());
        m.setCover(book.getCover());
        m.setStatus(book.getStatus());
        m.setIsbn(book.getIsbn());
        m.setDeleted(book.getDeleted());
        return m;
    }

    private BookDTO mergeMetaWithLiveStock(BookMetaDTO meta) {
        BookDTO dto = new BookDTO();
        dto.setId(meta.getId());
        dto.setTitle(meta.getTitle());
        dto.setAuthor(meta.getAuthor());
        dto.setPrice(meta.getPrice());
        dto.setDescription(meta.getDescription());
        dto.setCover(meta.getCover());
        dto.setStatus(meta.getStatus());
        dto.setIsbn(meta.getIsbn());
        dto.setDeleted(meta.getDeleted());
        // fetch live stock
        Integer stock = bookInventoryRepository.getStock(meta.getId());
        dto.setStock(stock == null ? 0 : stock);
        return dto;
    }
} 