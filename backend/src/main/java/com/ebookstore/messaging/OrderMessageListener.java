package com.ebookstore.messaging;

import com.ebookstore.dto.AsyncOrderRequestMessage;
import com.ebookstore.dto.OrderItemDTO;
import com.ebookstore.service.OrderService;
import com.ebookstore.repository.UserRepository;
import com.ebookstore.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderMessageListener {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ebookstore.kafka.topic.order-result}")
    private String orderResultTopic;

    public OrderMessageListener(OrderService orderService,
                                UserRepository userRepository,
                                KafkaTemplate<String, String> kafkaTemplate,
                                SimpMessagingTemplate messagingTemplate,
                                ApplicationEventPublisher eventPublisher) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.messagingTemplate = messagingTemplate;
        this.eventPublisher = eventPublisher;
    }

    @KafkaListener(topics = "${ebookstore.kafka.topic.order-request}", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderRequest(String messageJson) {
        AsyncOrderRequestMessage message = null;
        try {
            System.out.println("[Kafka][Consumer] order-requests -> " + messageJson);
            message = objectMapper.readValue(messageJson, AsyncOrderRequestMessage.class);

            // Load user context explicitly (avoid RequestContextHolder)
            User user = userRepository.findById(message.getUserId()).orElse(null);
            if (user == null) {
                sendResult(false, "用户不存在", null, message.getUserId());
                return;
            }

            List<OrderItemDTO> items;
            if (message.isDirectBuy()) {
                System.out.println("[Service] createDirectOrder starting for userId=" + user.getId());
                items = orderService.createDirectOrderForUser(user.getId(), message.getDirectItems());
            } else {
                System.out.println("[Service] createOrder starting for userId=" + user.getId() + ", cartItemIds=" + message.getCartItemIds());
                items = orderService.createOrderForUser(user.getId(), message.getCartItemIds());
            }

            System.out.println("[DB] Order persisted successfully, items size=" + items.size());

            // 发布订单处理完成事件，在事务提交后处理WebSocket推送
            eventPublisher.publishEvent(new com.ebookstore.event.OrderProcessedEvent(
                    true,
                    "下单成功，项目数: " + items.size(),
                    items,
                    message.getUserId()
            ));
        } catch (Exception e) {
            // 发布失败事件
            Long userId = (message != null) ? message.getUserId() : null;
            eventPublisher.publishEvent(new com.ebookstore.event.OrderProcessedEvent(
                    false,
                    "下单失败: " + e.getMessage(),
                    null,
                    userId
            ));
            e.printStackTrace();
        }
    }

    /**
     * 在事务提交后处理WebSocket推送和Kafka消息发送
     * 使用 @TransactionalEventListener 确保在事务提交后才执行
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderProcessedEvent(com.ebookstore.event.OrderProcessedEvent event) {
        try {
            System.out.println("[Event] 事务提交后处理订单结果，userId=" + event.getUserId());
            sendResult(event.isSuccess(), event.getMessage(), event.getItems(), event.getUserId());
        } catch (Exception e) {
            System.err.println("[Event] 处理订单结果失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendResult(boolean success, String msg, List<OrderItemDTO> items, Long userId) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("success", success);
        payload.put("message", msg);
        payload.put("userId", userId);
        payload.put("items", items);
        payload.put("timestamp", System.currentTimeMillis());

        String json = objectMapper.writeValueAsString(payload);

        // 1. 发送到 Kafka Topic（保留原有功能）
        kafkaTemplate.send(orderResultTopic, json);
        System.out.println("[Kafka][Producer] order-results <- " + json);

        // 2. 通过 WebSocket 推送给特定用户（新增功能）
        if (userId != null) {
            // 发送到 /user/{userId}/queue/order-result
            // 前端订阅 /user/queue/order-result 即可接收
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/order-result",
                    payload
            );
            System.out.println("[WebSocket] 推送订单结果给用户 " + userId);
        }
    }
}


