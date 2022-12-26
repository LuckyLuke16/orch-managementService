package com.example.managementservice.service;

import com.example.managementservice.exception.ShoppingCartContentNotFoundException;
import com.example.managementservice.model.ShoppingCartItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private ProductService productService;

    private ShoppingCartService shoppingCartService;

    private PaymentService paymentService;

    Logger logger = LoggerFactory.getLogger(OrderService.class);


    public OrderService(ProductService productService, ShoppingCartService shoppingCartService, PaymentService paymentService) {
        this.productService = productService;
        this.shoppingCartService = shoppingCartService;
        this.paymentService = paymentService;
    }

    public void makeOrder(String userId) {
        List<ShoppingCartItemDTO> itemsFromShoppingCart = this.fetchItemsToPayFor(userId);
        logger.info("{} items to pay for", itemsFromShoppingCart.size());
        this.updateStockOfItems(itemsFromShoppingCart);
        this.paymentService.makePayment();
    }

    private List<ShoppingCartItemDTO> fetchItemsToPayFor(String userId) {
        try {
            return this.shoppingCartService.fetchShoppingCartItems(userId);
        } catch (Exception e) {
            logger.warn("Order stopped because items from shopping cart were not found");
            throw new ShoppingCartContentNotFoundException();
        }
    }

    private void updateStockOfItems(List<ShoppingCartItemDTO> itemsFromShoppingCart) {
        try {
            this.productService.orderItems(itemsFromShoppingCart);
        } catch (Exception e) {
            logger.warn("Order stopped because i");
            throw new ShoppingCartContentNotFoundException();
        }
    }
}
