package com.soni.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrder {

    private String item;

    private int quantity;

    private double amount;

    private String paymentMethod;

    private Long orderId;

    private String address;
}