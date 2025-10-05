package com.ebookstore.controller;

import com.ebookstore.dto.CartItemDTO;
import com.ebookstore.dto.OrderDTO;
import com.ebookstore.dto.OrderItemDTO;
import com.ebookstore.entity.OrderItem;
import com.ebookstore.entity.Book;
import com.ebookstore.entity.Order;
import com.ebookstore.dto.UserInfoDTO;
import com.ebookstore.service.CartService;
import com.ebookstore.service.OrderService;
import com.ebookstore.service.OrderItemWriteService;
import com.ebookstore.service.AuthService;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ebookstore.dto.AsyncOrderRequestMessage;

import javax.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @Value("${ebookstore.kafka.topic.order-request}")
    private String orderRequestTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderController(OrderService orderService, AuthService authService, KafkaTemplate<String, String> kafkaTemplate) {
        this.orderService = orderService;
        this.authService = authService;
        this.kafkaTemplate = kafkaTemplate;
    }
    @Autowired
    private CartService cartService;//依赖注入：自动注入CartService实例 业务逻辑交给

    @Autowired
    private OrderItemWriteService orderItemWriteService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;//依赖注入：自动注入OrderService实例 业务逻辑交给

    @Autowired
    private AuthService authService;//依赖注入：自动注入AuthService实例 业务逻辑交给

    @GetMapping//order实体->OrderItemDTO->响应json返回给前端
    public ResponseEntity<Map<String, Object>> getOrders(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);//函数依赖 获取当前用户
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }

            List<OrderItemDTO> items = orderService.getOrders();//函数依赖 获取订单列表
            response.put("success", true);
            response.put("data", items);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取订单失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchOrders(
            @RequestParam(required = false) String bookName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }

            LocalDateTime start = null;
            LocalDateTime end = null;

            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            List<OrderDTO> orders = orderService.searchUserOrders(bookName, start, end);
            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "搜索订单失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<Map<String, Object>> getAllOrdersForAdmin(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }

            if (!"ADMIN".equals(currentUser.getRole())) {
                response.put("success", false);
                response.put("message", "权限不足");
                return ResponseEntity.status(403).body(response);
            }

            List<OrderDTO> orders = orderService.getAllOrders();
            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取所有订单失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/admin/search")
    public ResponseEntity<Map<String, Object>> searchAllOrdersForAdmin(
            @RequestParam(required = false) String bookName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }

            if (!"ADMIN".equals(currentUser.getRole())) {
                response.put("success", false);
                response.put("message", "权限不足");
                return ResponseEntity.status(403).body(response);
            }

            LocalDateTime start = null;
            LocalDateTime end = null;

            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDateTime.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }

            List<OrderDTO> orders = orderService.searchAllOrders(bookName, start, end);
            response.put("success", true);
            response.put("data", orders);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "搜索所有订单失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")//路径参数
    public ResponseEntity<Map<String, Object>> getOrderById(
            @PathVariable Long id,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserInfoDTO currentUser = authService.getCurrentUser(session);//函数依赖 获取当前用户
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.status(401).body(response);
            }

            OrderDTO order = orderService.getOrderById(id);//函数依赖 获取订单详情
            response.put("success", true);
            response.put("data", order);
            return ResponseEntity.ok(response);//Oreder实体->OrderDTO->响应json返回给前端

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取订单详情失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/create")//创建订单 支持从购物车结算和直接购买两种方式
    //请求json->Map<String, Object>->OrderItemDTO->Order实体->OrderItemDTO->响应json返回给前端
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

    @PostMapping("/create-async")
    public ResponseEntity<Map<String, Object>> createOrderAsync(
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

            System.out.println("[HTTP] /api/orders/create-async received from userId=" + currentUser.getId() + ", payload=" + payload);

            AsyncOrderRequestMessage message = new AsyncOrderRequestMessage();
            message.setUserId(currentUser.getId());
            boolean directBuy = Boolean.TRUE.equals(payload.get("directBuy"));
            message.setDirectBuy(directBuy);
            if (directBuy) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
                message.setDirectItems(items);
            } else {
                @SuppressWarnings("unchecked")
                List<Integer> ids = (List<Integer>) payload.get("cartItemIds");
                if (ids != null) {
                    List<Long> longIds = ids.stream().map(Integer::longValue).collect(java.util.stream.Collectors.toList());
                    message.setCartItemIds(longIds);
                }
            }

            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(orderRequestTopic, json);
            System.out.println("[Kafka][Producer] order-requests <- " + json);

            response.put("success", true);
            response.put("message", "下单请求已提交，正在异步处理");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "提交异步下单失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
} 