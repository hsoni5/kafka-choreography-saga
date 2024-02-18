package com.soni.inventory.service;

import com.soni.inventory.entity.Inventory;
import com.soni.inventory.event.DeliveryEvent;
import com.soni.inventory.event.PaymentEvent;
import com.soni.inventory.repository.InventoryRepository;
import com.soni.inventory.utils.InventoryConstant;
import com.soni.inventory.utils.JsonMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReverseInventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @KafkaListener(topics = InventoryConstant.REVERSED_INVENTORY, groupId = InventoryConstant.INVENTORY_GROUP)
    public void reverseInventory(String event) {
        log.info("inside reverse inventory for order: {}", event);
        DeliveryEvent deliveryEvent = JsonMapperUtil.readValue(event, DeliveryEvent.class);
        Iterable<Inventory> inventories = this.inventoryRepository.findByItem(deliveryEvent.getOrder().getItem());
        inventories.forEach(i -> {
            i.setQuantity(i.getQuantity() + deliveryEvent.getOrder().getQuantity());
            this.inventoryRepository.save(i);
        });
        PaymentEvent paymentEvent = PaymentEvent.builder()
                .type(InventoryConstant.REVERSED_PAYMENTS)
                .order(deliveryEvent.getOrder()).build();
        kafkaTemplate.send(InventoryConstant.REVERSED_PAYMENTS, paymentEvent);
    }
}
