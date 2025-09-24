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
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderMessageListener {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ebookstore.kafka.topic.order-result}")
    private String orderResultTopic;

    public OrderMessageListener(OrderService orderService,
                                UserRepository userRepository,
                                KafkaTemplate<String, String> kafkaTemplate) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "${ebookstore.kafka.topic.order-request}", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderRequest(String messageJson) {
        try {
            System.out.println("[Kafka][Consumer] order-requests -> " + messageJson);
            AsyncOrderRequestMessage message = objectMapper.readValue(messageJson, AsyncOrderRequestMessage.class);

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
            sendResult(true, "下单成功，项目数: " + items.size(), items, message.getUserId());
        } catch (Exception e) {
            try {
                sendResult(false, "下单失败: " + e.getMessage(), null, null);
            } catch (Exception ignored) {}
            e.printStackTrace();
        }
    }

    private void sendResult(boolean success, String msg, List<OrderItemDTO> items, Long userId) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("success", success);
        payload.put("message", msg);
        payload.put("userId", userId);
        payload.put("items", items);
        String json = objectMapper.writeValueAsString(payload);
        kafkaTemplate.send(orderResultTopic, json);
        System.out.println("[Kafka][Producer] order-results <- " + json);
    }
}


