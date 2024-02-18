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
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private KafkaTemplate<String, PaymentEvent> paymentEventKafkaTemplate;
    @Autowired
    private KafkaTemplate<String, OrderEvent> orderEventKafkaTemplate;
    @KafkaListener(topics = OrderConstant.NEW_ORDER, groupId = OrderConstant.ORDERS_GROUP)
    public void processPayment(String event) throws Exception {
        log.info("Received event for payment : {}", event);
        OrderEvent orderEvent = JsonMapperUtil.readValue(event, OrderEvent.class);
        CustomerOrder order = orderEvent.getOrder();
        Payment payment = Payment.builder().build();
        try {
            payment = Payment.builder().orderId(order.getOrderId())
                    .method(order.getPaymentMethod())
                    .amount(order.getAmount())
                    .status(PaymentConstant.SUCCESS).build();
            paymentRepository.save(payment);
            PaymentEvent paymentEvent = PaymentEvent.builder()
                    .type(PaymentConstant.PAYMENT_CREATED)
                    .order(orderEvent.getOrder())
                    .build();
            paymentEventKafkaTemplate.send(PaymentConstant.NEW_PAYMENT, paymentEvent);
        } catch (Exception exception) {
            log.info("Received exception for payment : {} for order id: {}", exception.getMessage(), order.getOrderId());
            payment.setOrderId(order.getOrderId());
            payment.setStatus(PaymentConstant.FAILED);
            paymentRepository.save(payment);

            OrderEvent reverseOrderEvent = OrderEvent.builder().
                    order(order)
                    .type(OrderConstant.REVERSED_ORDER).build();
            orderEventKafkaTemplate.send(OrderConstant.REVERSED_ORDER, reverseOrderEvent);
        }
    }
}
