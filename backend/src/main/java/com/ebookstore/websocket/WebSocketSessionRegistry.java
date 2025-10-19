package com.ebookstore.websocket;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 线程安全的会话注册表：维护 userId -> 多个 sessionId 的映射
 */
@Component
public class WebSocketSessionRegistry {

    private final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> userIdToSessionIds = new ConcurrentHashMap<>();

    public void addSession(String userId, String sessionId) {
        userIdToSessionIds.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(sessionId);
    }

    public void removeSession(String userId, String sessionId) {
        CopyOnWriteArraySet<String> set = userIdToSessionIds.get(userId);
        if (set != null) {
            set.remove(sessionId);
            if (set.isEmpty()) {
                userIdToSessionIds.remove(userId);
            }
        }
    }

    public Set<String> getSessionIds(String userId) {
        CopyOnWriteArraySet<String> set = userIdToSessionIds.get(userId);
        return set == null ? new CopyOnWriteArraySet<>() : set;
    }
}


