package com.ebookstore.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * 在 STOMP CONNECT 阶段，从 header 中读取 user-id，绑定为 Principal，
 * 并将会话记录到线程安全的注册表中；在 DISCONNECT 时清理。
 */
@Component
public class StompUserInterceptor implements ChannelInterceptor {

    private final WebSocketSessionRegistry registry;

    public StompUserInterceptor(WebSocketSessionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String userId = accessor.getFirstNativeHeader("user-id");
            if (userId != null && !userId.isEmpty()) {
                Principal principal = () -> userId;
                accessor.setUser(principal);
            }
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return;

        String sessionId = accessor.getSessionId();
        Principal principal = accessor.getUser();
        StompCommand command = accessor.getCommand();
        if (sessionId == null || command == null) return;

        if (StompCommand.CONNECT.equals(command) && principal != null) {
            registry.addSession(principal.getName(), sessionId);
        } else if (StompCommand.DISCONNECT.equals(command) && principal != null) {
            registry.removeSession(principal.getName(), sessionId);
        }
    }
}


