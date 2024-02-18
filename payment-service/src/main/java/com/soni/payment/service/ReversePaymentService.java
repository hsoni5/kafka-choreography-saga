package com.soni.payment.service;

import com.soni.payment.dto.CustomerOrder;
import com.soni.payment.entity.Payment;
import com.soni.payment.event.OrderEvent;
import com.soni.payment.event.PaymentEvent;
import com.soni.payment.repository.PaymentRepository;
import com.soni.payment.utils.JsonMapperUtil;
import com.soni.payment.utils.OrderConstant;
import com.soni.payment.utils.PaymentConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReversePaymentService {
    @Autowired
    private PaymentRepository repository;
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @KafkaListener(topics = PaymentConstant.REVERSED_PAYMENT, groupId = PaymentConstant.PAYMENT_GROUPS)
    public void reversePayment(String event) {
        log.info("Inside reverse payment for order {}", event);
        PaymentEvent paymentEvent = JsonMapperUtil.readValue(event, PaymentEvent.class);
        CustomerOrder order = paymentEvent.getOrder();
        Iterable<Payment> payments = this.repository.findByOrderId(order.getOrderId());
        payments.forEach(p -> {
            p.setStatus(PaymentConstant.FAILED);
            this.repository.save(p);
        });
        OrderEvent orderEvent = OrderEvent.builder().order(order).type(OrderConstant.REVERSED_ORDER).build();
        kafkaTemplate.send(OrderConstant.REVERSED_ORDER, orderEvent);
    }
}

