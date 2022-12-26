package com.example.managementservice.controller;

import com.example.managementservice.controller.controllerInterfaces.OrderOperations;
import com.example.managementservice.model.UserId;
import com.example.managementservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController implements OrderOperations {

    private final UserId userId;

    private final OrderService orderService;

    @Autowired
    public OrderController(UserId userId, OrderService orderService) {
        this.userId = userId;
        this.orderService = orderService;
    }

    public void createOrder() {
        try {
            String id = userId.getUserId();
            this.orderService.makeOrder(id);

        } catch (Exception e) {

        }
    }
}
