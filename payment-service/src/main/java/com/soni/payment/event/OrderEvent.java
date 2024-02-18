package com.soni.payment.event;

import com.soni.payment.dto.CustomerOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {

    private String type;

    private CustomerOrder order;
}
