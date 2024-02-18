package com.soni.inventory.service;

import com.soni.inventory.dto.CustomerOrder;
import com.soni.inventory.dto.InventoryDto;
import com.soni.inventory.entity.Inventory;
import com.soni.inventory.event.DeliveryEvent;
import com.soni.inventory.event.PaymentEvent;
import com.soni.inventory.repository.InventoryRepository;
import com.soni.inventory.utils.InventoryConstant;
import com.soni.inventory.utils.JsonMapperUtil;
import com.soni.inventory.utils.PaymentConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryService {
    @Autowired
    private InventoryRepository repository;
    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaPaymentTemplate;
    @Autowired
    private KafkaTemplate<String, DeliveryEvent> kafkaTemplate;

    @KafkaListener(topics = PaymentConstant.NEW_PAYMENT, groupId = PaymentConstant.PAYMENT_GROUP)
    public void updateInventory(String payment) {
        log.info("Inside update inventory for order: {}", payment);
        PaymentEvent paymentEvent = JsonMapperUtil.readValue(payment, PaymentEvent.class);
        CustomerOrder customerOrder = paymentEvent.getOrder();
        try {
            Iterable<Inventory> inventories = this.repository.findByItem(customerOrder.getItem());
            boolean isExists = inventories.iterator().hasNext();
            if (!isExists) {
                log.info("Inventory not exist so reverting the order");
                throw new Exception("Item not available in inventory");
            }
            inventories.forEach(i -> {
                i.setQuantity(i.getQuantity() - customerOrder.getQuantity());
                this.repository.save(i);
            });
            DeliveryEvent deliveryEvent = DeliveryEvent.builder()
                    .type(InventoryConstant.DELIVERY_UPDATED)
                    .order(customerOrder).build();
            kafkaTemplate.send(InventoryConstant.NEW_DELIVERY, deliveryEvent);

        } catch (Exception e) {
            log.error("Item not available hence reverse payment : {}", e.getMessage());
            PaymentEvent p = PaymentEvent.builder()
                    .type(InventoryConstant.REVERSED_PAYMENTS)
                    .order(customerOrder)
                    .build();
            kafkaPaymentTemplate.send(InventoryConstant.REVERSED_PAYMENTS, p);
        }
    }

    public void addItem(InventoryDto inventoryDto) {
        repository.save(Inventory.builder().item(inventoryDto.getItem()).quantity(inventoryDto.getQuantity()).build());
    }
}
