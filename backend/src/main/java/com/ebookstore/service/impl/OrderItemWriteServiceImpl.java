package com.ebookstore.service.impl;

import com.ebookstore.entity.OrderItem;
import com.ebookstore.repository.OrderItemRepository;
import com.ebookstore.service.OrderItemWriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * OrderItem 写入服务实现
 * 演示 7 种不同的事务传播属性
 */
@Service
public class OrderItemWriteServiceImpl implements OrderItemWriteService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrderItem> saveRequired(List<OrderItem> items) {
        System.out.println("【传播属性：REQUIRED】保存 OrderItem，数量: " + items.size());
        return orderItemRepository.saveAll(items);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OrderItem> saveRequiresNew(List<OrderItem> items) {
        System.out.println("【传播属性：REQUIRES_NEW】保存 OrderItem，数量: " + items.size());
        return orderItemRepository.saveAll(items);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public List<OrderItem> saveNested(List<OrderItem> items) {
        System.out.println("【saveNested】当前类: " + this.getClass());
        System.out.println("【传播属性：NESTED】保存 OrderItem，数量: " + items.size());
        return orderItemRepository.saveAll(items);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<OrderItem> saveMandatory(List<OrderItem> items) {
        System.out.println("【传播属性：MANDATORY】保存 OrderItem，数量: " + items.size());
        return orderItemRepository.saveAll(items);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<OrderItem> saveSupports(List<OrderItem> items) {
        System.out.println("【传播属性：SUPPORTS】保存 OrderItem，数量: " + items.size());
        return orderItemRepository.saveAll(items);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<OrderItem> saveNotSupported(List<OrderItem> items) {
        System.out.println("【传播属性：NOT_SUPPORTED】保存 OrderItem，数量: " + items.size());
        return orderItemRepository.saveAll(items);
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    public List<OrderItem> saveNever(List<OrderItem> items) {
        System.out.println("【传播属性：NEVER】保存 OrderItem，数量: " + items.size());
        return orderItemRepository.saveAll(items);
    }
}

