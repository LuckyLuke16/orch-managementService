package com.example.managementservice.service;

import com.example.managementservice.exception.AddressException;
import com.example.managementservice.exception.PaymentFailedException;
import com.example.managementservice.exception.ShoppingCartContentNotFoundException;
import com.example.managementservice.exception.StockUpdateException;
import com.example.managementservice.model.Address;
import com.example.managementservice.model.OrderDTO;
import com.example.managementservice.model.ShoppingCartItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderService {

    private ProductService productService;

    private ShoppingCartService shoppingCartService;

    private PaymentService paymentService;

    private final RestTemplate restTemplate;

    private final String ORDER_SERVICE_URL = "http://localhost:8084";

    Logger logger = LoggerFactory.getLogger(OrderService.class);


    public OrderService(ProductService productService, ShoppingCartService shoppingCartService, PaymentService paymentService, RestTemplate restTemplate) {
        this.productService = productService;
        this.shoppingCartService = shoppingCartService;
        this.paymentService = paymentService;
        this.restTemplate = restTemplate;
    }

    public void makeOrder(String userId, OrderDTO orderDetails) {
        if(orderDetails.getAddress() == null)
            throw new AddressException();

        List<ShoppingCartItemDTO> itemsFromShoppingCart = this.fetchItemsToPayFor(userId);
        logger.info("{} items to pay for", itemsFromShoppingCart.size());
        this.updateStockOfItems(itemsFromShoppingCart);
        long paymentId = this.fulfillPayment(orderDetails.getPaymentMethod(), userId, itemsFromShoppingCart);
        this.saveOrder(userId, paymentId, orderDetails.getAddress());
    }

    private void saveOrder(String userId, long paymentId, Address address) {
        try{
            ResponseEntity<Long> response = restTemplate.postForEntity(ORDER_SERVICE_URL + "/orders"
                    + "?userId=" + userId + "&paymentId=" + paymentId, address, Long.class);

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody() && response.getBody() != null) {
                logger.info("Payment with payment id: {} was successful", response.getBody());

            }
        }catch(StockUpdateException e){
            logger.warn("Item stock could not be reset",e);
        }
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
            logger.warn("Order stopped because the stock of some items could not be updated");
            throw new ShoppingCartContentNotFoundException();
        }
    }
    private long fulfillPayment(String paymentMethod, String userId, List<ShoppingCartItemDTO> itemsToPayFor) {
        try {
            return this.paymentService.makePayment(paymentMethod, userId, itemsToPayFor);
        } catch (Exception e) {
            logger.warn("Order stopped because the payment failed");
            this.resetFailedItemInventory(itemsToPayFor);
            throw new PaymentFailedException();
        }
    }

    private void resetFailedItemInventory(List<ShoppingCartItemDTO> failedItems){
        try{
            this.productService.resetStock(failedItems);
            logger.info("Items stock was successfully reset");
        }catch(StockUpdateException e){
            logger.warn("Item stock could not be reset",e);
        }
    }
}
