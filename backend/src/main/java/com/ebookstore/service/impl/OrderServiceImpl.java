package com.ebookstore.service.impl;

import com.ebookstore.dto.OrderDTO;
import com.ebookstore.dto.OrderItemDTO;
import com.ebookstore.dto.UserInfoDTO;
import com.ebookstore.entity.CartItem;
import com.ebookstore.entity.Order;
import com.ebookstore.entity.OrderItem;
import com.ebookstore.entity.User;
import com.ebookstore.entity.Book;
import com.ebookstore.repository.CartItemRepository;
import com.ebookstore.repository.OrderRepository;
import com.ebookstore.repository.OrderItemRepository;
import com.ebookstore.repository.BookRepository;
import com.ebookstore.repository.UserRepository;
import com.ebookstore.service.OrderService;
import com.ebookstore.service.OrderItemWriteService;
import com.ebookstore.service.UserService;
import com.ebookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderItemWriteService orderItemWriteService;

    @Autowired
    private javax.persistence.EntityManager entityManager;


    @Override
    public List<OrderItemDTO> getOrders() {
        User user = userService.getCurrentUser();
        List<OrderItemDTO> orderItems = new ArrayList<>();

        orderRepository.findByUserOrderByOrderDateDesc(user).forEach(order -> {
            order.getItems().forEach(item -> {
                orderItems.add(convertToDTO(item));
            });
        });

        return orderItems;
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        User user = userService.getCurrentUser();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + id + " 的订单"));

        // 安全检查
        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("无权查看此订单");
        }

        return convertToOrderDTO(order);
    }

    @Override
    @Transactional
    public List<OrderItemDTO> createOrder(List<Long> cartItemIds) {
        User user = userService.getCurrentUser();
        return createOrderForUser(user.getId(), cartItemIds);
    }

    @Override
    @Transactional
    public List<OrderItemDTO> createOrderForUser(Long userId, List<Long> cartItemIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        // 获取要购买的购物车项
        List<CartItem> selectedItems;
        if (cartItemIds == null || cartItemIds.isEmpty()) {
            // 如果没有指定ID，则获取所有已选中的项目
            selectedItems = cartItemRepository.findByUserAndSelected(user, true);
        } else {
            // 否则获取指定ID的项目
            selectedItems = cartItemRepository.findAllById(cartItemIds).stream()
                    .filter(item -> item.getUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
        }

        if (selectedItems.isEmpty()) {
            throw new IllegalArgumentException("没有可购买的商品");
        }

        // 先检查所有商品的库存
        for (CartItem cartItem : selectedItems) {
            if (!bookService.checkStock(cartItem.getBook().getId(), cartItem.getQuantity())) {
                throw new IllegalArgumentException("商品《" + cartItem.getBook().getTitle() + "》库存不足，需要" + cartItem.getQuantity() + "本");
            }
        }

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("COMPLETED"); // 直接设置为已完成状态
        order.setShippingAddress(user.getAddress()); // 设置配送地址为用户地址

        // 计算总金额并添加订单项
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : selectedItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getBook().getPrice());
            orderItem.setSubtotal(cartItem.getBook().getPrice().multiply(new BigDecimal(cartItem.getQuantity())));

            // 使用辅助方法建立关系
            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        // 设置订单总金额
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);
        System.out.println("【事务测试】Order已保存到数据库，ID: " + order.getId());

        // 方式1: REQUIRED（默认） - 加入当前事务，如果当前没有事务则创建新事务
         orderItemWriteService.saveRequired(order.getItems());
         System.out.println("【事务测试】OrderItem已保存到数据库，数量: " + order.getItems().size());

        // 方式2: REQUIRES_NEW - 总是创建新事务，挂起当前事务（如果有）
//         orderItemWriteService.saveRequiresNew(order.getItems());
//
//         System.out.println("【事务测试】OrderItem已保存（REQUIRES_NEW），数量: " + order.getItems().size());

        // 方式3: NESTED - 如果当前有事务，则在嵌套事务中执行
        // orderItemWriteService.saveNested(order.getItems());
        // entityManager.flush();
        // System.out.println("【事务测试】OrderItem已保存（NESTED），数量: " + order.getItems().size());

        // 方式4: MANDATORY - 必须在事务中执行，否则抛出异常
        // orderItemWriteService.saveMandatory(order.getItems());
        // entityManager.flush();
        // System.out.println("【事务测试】OrderItem已保存（MANDATORY），数量: " + order.getItems().size());

        // 方式5: SUPPORTS - 如果当前有事务则加入，没有则以非事务方式执行
        // orderItemWriteService.saveSupports(order.getItems());
        // entityManager.flush();
        // System.out.println("【事务测试】OrderItem已保存（SUPPORTS），数量: " + order.getItems().size());

        // 方式6: NOT_SUPPORTED - 以非事务方式执行，如果当前有事务则挂起
        // orderItemWriteService.saveNotSupported(order.getItems());
        // entityManager.flush();
        // System.out.println("【事务测试】OrderItem已保存（NOT_SUPPORTED），数量: " + order.getItems().size());

        // 方式7: NEVER - 以非事务方式执行，如果当前有事务则抛出异常
        // orderItemWriteService.saveNever(order.getItems());
        // entityManager.flush();
        // System.out.println("【事务测试】OrderItem已保存（NEVER），数量: " + order.getItems().size());



        // ========== 测试回滚：在此处制造除0异常 ==========
        // 取消注释下面这行可以测试不同传播属性下的回滚行为
        // System.out.println("【事务测试】即将抛出除0异常，测试回滚...");
        // int testRollback = 1 / 0;

        // ========== 第三步：减少库存 ==========
        for (CartItem cartItem : selectedItems) {
            if (!bookService.reduceStock(cartItem.getBook().getId(), cartItem.getQuantity())) {
                throw new RuntimeException("减少《" + cartItem.getBook().getTitle() + "》库存失败");
            }
        }
        entityManager.flush();

        // ========== 第四步：删除购物车中的相关项目 ==========
        cartItemRepository.deleteAll(selectedItems);
        entityManager.flush();

        // 转换返回数据
        return order.getItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    private OrderItemDTO convertToDTO(OrderItem orderItem) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String orderDate = orderItem.getOrder().getOrderDate().format(formatter);

        return new OrderItemDTO(
                orderItem.getId(),
                orderItem.getBook().getId(),
                orderItem.getBook().getTitle(),
                orderItem.getBook().getAuthor(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getSubtotal(),
                orderDate
        );
    }

    private OrderDTO convertToOrderDTO(Order order) {
        UserInfoDTO userDto = new UserInfoDTO(
                order.getUser().getId(),
                order.getUser().getUserAuth().getUsername(),
                order.getUser().getName(),
                order.getUser().getEmail(),
                order.getUser().getUserAuth().getRole(),
                order.getUser().getPhone(),
                order.getUser().getAddress()
        );

        return new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                userDto,
                order.getOrderItems().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public List<OrderItemDTO> createDirectOrder(List<Map<String, Object>> items) {
        User user = userService.getCurrentUser();//函数依赖
        return createDirectOrderForUser(user.getId(), items);
    }

    @Override
    @Transactional
    public List<OrderItemDTO> createDirectOrderForUser(Long userId, List<Map<String, Object>> items) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("COMPLETED");
        order.setShippingAddress(user.getAddress()); // 设置配送地址为用户地址

        // 先检查所有商品的库存
        for (Map<String, Object> item : items) {
            Long bookId = Long.valueOf(item.get("bookId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());

            if (!bookService.checkStock(bookId, quantity)) {
                Book book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + bookId + " 的书籍"));
                throw new IllegalArgumentException("商品《" + book.getTitle() + "》库存不足，需要" + quantity + "本");
            }
        }

        // 计算总金额并添加订单项
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map<String, Object> item : items) {
            Long bookId = Long.valueOf(item.get("bookId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());

            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + bookId + " 的书籍"));

            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(book.getPrice());
            orderItem.setSubtotal(book.getPrice().multiply(BigDecimal.valueOf(quantity)));

            // 使用辅助方法建立关系
            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);

        order = orderRepository.save(order);
        entityManager.flush();
        System.out.println("【事务测试】Order已保存到数据库，ID: " + order.getId());

        // 方式1: REQUIRED（默认） - 加入当前事务，如果当前没有事务则创建新事务
         orderItemWriteService.saveRequired(order.getItems());
        entityManager.flush();
         System.out.println("【事务测试】OrderItem已保存到数据库，数量: " + order.getItems().size());
//         方式2: REQUIRES_NEW - 总是创建新事务，挂起当前事务（如果有）
//        orderItemWriteService.saveRequiresNew(order.getItems());
//        System.out.println("【事务测试】OrderItem已保存（REQUIRES_NEW），数量: " + order.getItems().size());
        // 方式3: NESTED - 如果当前有事务，则在嵌套事务中执行
//         orderItemWriteService.saveNested(order.getItems());
//         System.out.println("【事务测试】OrderItem已保存（NESTED），数量: " + order.getItems().size());
        // 方式4: MANDATORY - 必须在事务中执行，否则抛出异常
//         orderItemWriteService.saveMandatory(order.getItems());
//         System.out.println("【事务测试】OrderItem已保存（MANDATORY），数量: " + order.getItems().size());
        // 方式5: SUPPORTS - 如果当前有事务则加入，没有则以非事务方式执行
//         orderItemWriteService.saveSupports(order.getItems());
//         System.out.println("【事务测试】OrderItem已保存（SUPPORTS），数量: " + order.getItems().size());
        // 方式6: NOT_SUPPORTED - 以非事务方式执行，如果当前有事务则挂起
//         orderItemWriteService.saveNotSupported(order.getItems());
//         System.out.println("【事务测试】OrderItem已保存（NOT_SUPPORTED），数量: " + order.getItems().size());
        // 方式7: NEVER - 以非事务方式执行，如果当前有事务则抛出异常
//         orderItemWriteService.saveNever(order.getItems());
//         System.out.println("【事务测试】OrderItem已保存（NEVER），数量: " + order.getItems().size());
//         System.out.println("【事务测试】即将抛出除0异常，测试回滚...");
//         int testRollback = 1 / 0;

        // ========== 第三步：减少库存 ==========
        for (Map<String, Object> item : items) {
            Long bookId = Long.valueOf(item.get("bookId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());

            if (!bookService.reduceStock(bookId, quantity)) {
                Book book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new EntityNotFoundException("未找到ID为 " + bookId + " 的书籍"));
                throw new RuntimeException("减少《" + book.getTitle() + "》库存失败");
            }
            entityManager.flush();
        }

        // 转换返回数据
        return order.getItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> searchUserOrders(String bookName, LocalDateTime startDate, LocalDateTime endDate) {
        User user = userService.getCurrentUser();
        List<Order> orders = orderRepository.findByUserAndBookNameAndDateRange(
                user, bookName, startDate, endDate);
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> searchAllOrders(String bookName, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByBookNameAndDateRange(
                bookName, startDate, endDate);
        return orders.stream()
                .map(this::convertToOrderDTO)
                .collect(Collectors.toList());
    }
}