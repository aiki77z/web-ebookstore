package com.ebookstore.event;

import com.ebookstore.dto.OrderItemDTO;
import java.util.List;

/**
 * 订单处理完成事件
 * 用于在事务提交后发送WebSocket消息
 */
public class OrderProcessedEvent {
    private final boolean success;
    private final String message;
    private final List<OrderItemDTO> items;
    private final Long userId;
    private final long timestamp;

    public OrderProcessedEvent(boolean success, String message, List<OrderItemDTO> items, Long userId) {
        this.success = success;
        this.message = message;
        this.items = items;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public Long getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
