package com.soni.inventory.event;

import com.soni.inventory.dto.CustomerOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder@AllArgsConstructor
@NoArgsConstructor
public class DeliveryEvent {
    private String type;
    private CustomerOrder order;
}