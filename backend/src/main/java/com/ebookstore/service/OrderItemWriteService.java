package com.ebookstore.service;

import com.ebookstore.entity.OrderItem;
import java.util.List;

/**
 * OrderItem 写入服务
 * 用于演示不同的事务传播属性
 */
public interface OrderItemWriteService {

    // 方式1: REQUIRED（默认） - 加入当前事务，如果当前没有事务则创建新事务
    List<OrderItem> saveRequired(List<OrderItem> items);

    // 方式2: REQUIRES_NEW - 总是创建新事务，挂起当前事务
    List<OrderItem> saveRequiresNew(List<OrderItem> items);

    // 方式3: NESTED - 如果当前有事务，则在嵌套事务中执行
    List<OrderItem> saveNested(List<OrderItem> items);

    // 方式4: MANDATORY - 必须在事务中执行，否则抛出异常
    List<OrderItem> saveMandatory(List<OrderItem> items);

    // 方式5: SUPPORTS - 如果当前有事务则加入，没有则以非事务方式执行
    List<OrderItem> saveSupports(List<OrderItem> items);

    // 方式6: NOT_SUPPORTED - 以非事务方式执行，如果当前有事务则挂起
    List<OrderItem> saveNotSupported(List<OrderItem> items);

    // 方式7: NEVER - 以非事务方式执行，如果当前有事务则抛出异常
    List<OrderItem> saveNever(List<OrderItem> items);
}

