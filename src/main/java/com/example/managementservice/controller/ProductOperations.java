package com.example.managementservice.controller;

import com.example.managementservice.model.ItemDTO;
import com.example.managementservice.model.ItemDetailDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/items")
public interface ProductOperations {

    @GetMapping
    List<ItemDTO> fetchAllItems();

    @GetMapping
    ItemDetailDTO fetchSingleItem();

}
