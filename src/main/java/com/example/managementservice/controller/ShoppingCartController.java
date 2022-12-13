package com.example.managementservice.controller;

import com.example.managementservice.controller.controllerInterfaces.ShoppingCartOperations;
import com.example.managementservice.model.ItemDTO;
import com.example.managementservice.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class ShoppingCartController implements ShoppingCartOperations {

    ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }
    public List<ItemDTO> fetchShoppingCartContent() {
        List<ItemDTO> allShoppingCartItems;
        try {
            allShoppingCartItems = shoppingCartService.fetchShoppingCartItems();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return allShoppingCartItems;
    }

    public void deleteItem(int itemId) {
        try {
            shoppingCartService.deleteShoppingCartItem(itemId);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void addItem(int itemId) {
        try {
            shoppingCartService.deleteShoppingCartItem(itemId);
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
