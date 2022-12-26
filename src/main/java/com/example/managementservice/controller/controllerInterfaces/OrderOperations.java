package com.example.managementservice.controller.controllerInterfaces;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/order")
public interface OrderOperations {

    @PostMapping
    void createOrder();
}
