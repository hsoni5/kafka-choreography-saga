package com.soni.order.service.listner;

import com.soni.order.entity.Order;
import com.soni.order.event.OrderEvent;
import com.soni.order.repository.OrderRepository;
import com.soni.order.utils.JsonMapperUtil;
import com.soni.order.utils.OrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class ReverseOrder {
    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics = OrderConstant.REVERSED_ORDER, groupId = OrderConstant.ORDERS_GROUP)
    public void reverseOrder(String event) {
        log.info("Inside reverse order for reverse order id {}", event);
        OrderEvent orderEvent = JsonMapperUtil.readValue(event, OrderEvent.class);
        Optional<Order> order = orderRepository.findById(orderEvent.getOrder().getOrderId());
        order.ifPresent(o -> {
            o.setStatus(OrderConstant.FAILED);
            this.orderRepository.save(o);
        });
    }
}
