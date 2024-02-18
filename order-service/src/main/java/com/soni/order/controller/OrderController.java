package com.soni.order.controller;

import com.soni.order.dto.CustomerOrder;
import com.soni.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping
    public String createOrder(@RequestBody CustomerOrder customerOrder){
       return orderService.createOrder(customerOrder);
    }

}
