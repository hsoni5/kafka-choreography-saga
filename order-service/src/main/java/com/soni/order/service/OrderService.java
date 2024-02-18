package com.soni.order.service;

import com.soni.order.dto.CustomerOrder;
import com.soni.order.entity.Order;
import com.soni.order.event.OrderEvent;
import com.soni.order.repository.OrderRepository;
import com.soni.order.utils.OrderConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate;

    public String createOrder(CustomerOrder customerOrder) {
        Order order = Order.builder().build();
        try {
            order = Order.builder().item(customerOrder.getItem())
                    .amount(customerOrder.getAmount())
                    .quantity(customerOrder.getQuantity())
                    .status(OrderConstant.CREATED)
                    .build();
            orderRepository.save(order);
            customerOrder.setOrderId(order.getId());
            OrderEvent orderEvent = OrderEvent.builder().order(customerOrder)
                    .type(OrderConstant.ORDER_CREATED)
                    .build();
            orderEventKafkaTemplate.send(OrderConstant.NEW_ORDER, orderEvent);
            return OrderConstant.ORDER_CREATED;
        } catch (Exception exception) {
            order.setStatus(OrderConstant.FAILED);
            orderRepository.save(order);
            return OrderConstant.FAILED;
        }
    }
}
