package com.soni.delivery.service;

import com.soni.delivery.dto.CustomerOrder;
import com.soni.delivery.entity.Delivery;
import com.soni.delivery.event.DeliveryEvent;
import com.soni.delivery.repository.DeliveryRepository;
import com.soni.delivery.utils.DeliveryConstant;
import com.soni.delivery.utils.InventoryConstant;
import com.soni.delivery.utils.JsonMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    private KafkaTemplate<String, DeliveryEvent> deliveryEventKafkaTemplate;

    @KafkaListener(topics = DeliveryConstant.NEW_DELIVERY, groupId = DeliveryConstant.DELIVERY_GROUP)
    public void processDelivery(String event) {
        log.info("Inside delivery order for order: {}", event);
        Delivery delivery = Delivery.builder().build();
        DeliveryEvent deliveryEvent = JsonMapperUtil.readValue(event, DeliveryEvent.class);
        CustomerOrder customerOrder = deliveryEvent.getOrder();
        try {
            if (ObjectUtils.isEmpty(customerOrder.getAddress())) {
                throw new Exception("Address not found");
            }
            delivery = Delivery.builder()
                    .address(customerOrder.getAddress())
                    .orderId(customerOrder.getOrderId())
                    .status(DeliveryConstant.SUCCESS)
                    .build();
            deliveryRepository.save(delivery);
        } catch (Exception e) {
            log.error("Delivery error : {}", e.getMessage());
            delivery = Delivery.builder()
                    .orderId(customerOrder.getOrderId())
                    .status(DeliveryConstant.FAILED)
                    .build();
            deliveryRepository.save(delivery);
            deliveryEvent = DeliveryEvent.builder().type(InventoryConstant.REVERSED_INVENTORY)
                    .order(customerOrder).build();
            deliveryEventKafkaTemplate.send(InventoryConstant.REVERSED_INVENTORY, deliveryEvent);
        }
    }
}
