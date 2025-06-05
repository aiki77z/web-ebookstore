package com.ebookstore.controller;

import com.ebookstore.dto.CartItemDTO;
import com.ebookstore.dto.OrderDTO;
import com.ebookstore.dto.OrderItemDTO;
import com.ebookstore.dto.UserInfoDTO;
import com.ebookstore.service.CartService;
import com.ebookstore.service.OrderService;
import com.ebookstore.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OrderController {
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private AuthService authService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrders(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            List<OrderItemDTO> items = orderService.getOrders();
            response.put("success", true);
            response.put("data", items);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取订单失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getOrderById(
            @PathVariable Long id,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            OrderDTO order = orderService.getOrderById(id);
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取订单详情失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody Map<String, Object> payload,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }
            
            List<OrderItemDTO> orderItems;
            
            // 处理直接购买的情况
            if (payload.containsKey("directBuy") && Boolean.TRUE.equals(payload.get("directBuy"))) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
                if (items == null || items.isEmpty()) {
                    response.put("success", false);
                    response.put("message", "订单商品列表为空");
                    return ResponseEntity.badRequest().body(response);
                }
                orderItems = orderService.createDirectOrder(items);
            } else {
                // 处理从购物车结算的情况
                List<Long> cartItemIds = null;
                if (payload.containsKey("cartItemIds")) {
                    try {
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
                        response.put("success", false);
                        response.put("message", "无效的购物车项ID");
                        return ResponseEntity.badRequest().body(response);
                    }
                }

                if (cartItemIds == null || cartItemIds.isEmpty()) {
                    response.put("success", false);
                    response.put("message", "请选择要购买的商品");
                    return ResponseEntity.badRequest().body(response);
                }

                orderItems = orderService.createOrder(cartItemIds);
            }
            
            response.put("success", true);
            response.put("message", "订单创建成功");
            response.put("data", orderItems);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "创建订单失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 