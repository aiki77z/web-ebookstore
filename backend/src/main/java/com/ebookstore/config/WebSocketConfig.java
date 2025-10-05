package com.ebookstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 配置类
 * 用于实现订单处理结果的实时推送
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的消息代理，用于向客户端发送消息
        // /topic 用于广播消息（一对多）
        // /queue 用于点对点消息（一对一）
        config.enableSimpleBroker("/topic", "/queue");

        // 设置客户端发送消息的前缀
        config.setApplicationDestinationPrefixes("/app");

        // 设置用户目的地前缀（用于点对点消息）
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册 STOMP 端点，客户端通过此端点连接 WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000") // 允许前端跨域
                .withSockJS(); // 启用 SockJS 降级选项（兼容不支持 WebSocket 的浏览器）
    }
}
