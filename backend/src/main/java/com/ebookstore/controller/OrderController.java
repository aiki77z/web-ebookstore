package com.ebookstore.controller;

import com.ebookstore.dto.CartItemDTO;
import com.ebookstore.dto.OrderDTO;
import com.ebookstore.dto.OrderItemDTO;
import com.ebookstore.service.CartService;
import com.ebookstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public ResponseEntity<List<OrderItemDTO>> getOrders() {
        try {
            List<OrderItemDTO> items = orderService.getOrders();
            return ResponseEntity.ok(items.isEmpty() ? new ArrayList<>() : items);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>()); // 返回空数组而不是错误
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<List<OrderItemDTO>> createOrder(@RequestBody Map<String, Object> payload) {
        try {
            // 处理直接购买的情况
            if (payload.containsKey("directBuy") && Boolean.TRUE.equals(payload.get("directBuy"))) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
                if (items == null || items.isEmpty()) {
                    return ResponseEntity.badRequest().build();
                }

                return ResponseEntity.ok(orderService.createDirectOrder(items));
            }

            // 处理从购物车结算的情况
            List<Long> cartItemIds = null;
            if (payload.containsKey("cartItemIds")) {
                try {
                    // 尝试获取购物车项目ID列表
                    Object cartItemIdsObj = payload.get("cartItemIds");
                    if (cartItemIdsObj instanceof List) {
                        cartItemIds = ((List<?>) cartItemIdsObj).stream()
                                .map(item -> {
                                    if (item instanceof Integer) {
                                        return ((Integer) item).longValue();
                                    } else if (item instanceof Long) {
                                        return (Long) item;
                                    } else if (item instanceof String) {
                                        return Long.parseLong((String) item);
                                    } else if (item instanceof Number) {
                                        return ((Number) item).longValue();
                                    }
                                    return null;
                                })
                                .filter(id -> id != null)
                                .collect(Collectors.toList());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.badRequest()
                            .body(Collections.emptyList());
                }
            }

            // 检查cartItemIds是否为空，如果为空返回错误
            if (cartItemIds == null || cartItemIds.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Collections.emptyList());
            }

            return ResponseEntity.ok(orderService.createOrder(cartItemIds));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
} 