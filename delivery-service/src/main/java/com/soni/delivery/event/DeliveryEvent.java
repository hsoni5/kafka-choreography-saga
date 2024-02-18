package com.soni.delivery.event;

import com.soni.delivery.dto.CustomerOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryEvent {
    private String type;
    private CustomerOrder order;
}
