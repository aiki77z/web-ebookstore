package com.ebookstore.dto;

import java.util.List;
import java.util.Map;

/**
 * Message sent to Kafka to request async order creation.
 */
public class AsyncOrderRequestMessage {
    private Long userId;
    private boolean directBuy;
    private List<Long> cartItemIds; // optional when directBuy=false
    private List<Map<String, Object>> directItems; // optional when directBuy=true, contains bookId, quantity

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public boolean isDirectBuy() { return directBuy; }
    public void setDirectBuy(boolean directBuy) { this.directBuy = directBuy; }

    public List<Long> getCartItemIds() { return cartItemIds; }
    public void setCartItemIds(List<Long> cartItemIds) { this.cartItemIds = cartItemIds; }

    public List<Map<String, Object>> getDirectItems() { return directItems; }
    public void setDirectItems(List<Map<String, Object>> directItems) { this.directItems = directItems; }
}


