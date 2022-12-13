package com.example.managementservice.controller.controllerInterfaces;

import com.example.managementservice.model.ItemDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/shopping-cart")
public interface ShoppingCartOperations {

    @GetMapping
    List<ItemDTO> fetchShoppingCartContent();

    @DeleteMapping("/{itemId}")
    void deleteItem(@PathVariable int itemId);

    @PostMapping("/{itemId}")
    void addItem(@PathVariable int itemId);
}
