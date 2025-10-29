package com.ebookstore.service;

import com.ebookstore.dto.BookMetaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class BookCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.cache.book.ttl-seconds:600}")
    private long bookTtlSeconds;

    public BookCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String keyForBook(Long id) {
        return "book:meta:" + id;
    }

    private String keyForAllList() {
        return "book:list:all";
    }

    public BookMetaDTO getBookMeta(Long id) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            Object cached = ops.get(keyForBook(id));
            if (cached instanceof BookMetaDTO) {
                System.out.println("[Redis] HIT book meta id=" + id);
                return (BookMetaDTO) cached;
            }
            System.out.println("[Redis] MISS book meta id=" + id);
        } catch (DataAccessException e) {
            System.out.println("[Redis] ERROR getBookMeta id=" + id + ": " + e.getMessage());
        }
        return null;
    }

    public void putBookMeta(BookMetaDTO dto) {
        if (dto == null || dto.getId() == null) return;
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            ops.set(keyForBook(dto.getId()), dto, bookTtlSeconds, TimeUnit.SECONDS);
            System.out.println("[Redis] PUT book meta id=" + dto.getId());
        } catch (DataAccessException e) {
            System.out.println("[Redis] ERROR putBookMeta id=" + dto.getId() + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<BookMetaDTO> getAllBookMetaList() {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            Object cached = ops.get(keyForAllList());
            if (cached instanceof List) {
                System.out.println("[Redis] HIT all books list");
                return (List<BookMetaDTO>) cached;
            }
            System.out.println("[Redis] MISS all books list");
        } catch (DataAccessException e) {
            System.out.println("[Redis] ERROR getAllBookMetaList: " + e.getMessage());
        }
        return null;
    }

    public void putAllBookMetaList(List<BookMetaDTO> list) {
        if (list == null) return;
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            ops.set(keyForAllList(), new ArrayList<>(list), bookTtlSeconds, TimeUnit.SECONDS);
            System.out.println("[Redis] PUT all books list size=" + list.size());
        } catch (DataAccessException e) {
            System.out.println("[Redis] ERROR putAllBookMetaList: " + e.getMessage());
        }
    }

    public void evictBook(Long id) {
        try {
            redisTemplate.delete(keyForBook(id));
            redisTemplate.delete(keyForAllList());
            System.out.println("[Redis] EVICT keys for book id=" + id);
        } catch (DataAccessException e) {
            System.out.println("[Redis] ERROR evictBook id=" + id + ": " + e.getMessage());
        }
    }

    public void evictAllList() {
        try {
            redisTemplate.delete(keyForAllList());
            System.out.println("[Redis] EVICT all books list");
        } catch (DataAccessException e) {
            System.out.println("[Redis] ERROR evictAllList: " + e.getMessage());
        }
    }
}



