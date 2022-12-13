package com.example.managementservice.service;

import com.example.managementservice.exception.NoItemsFoundException;
import com.example.managementservice.exception.ShoppingCartContentNotFoundException;
import com.example.managementservice.model.ItemDetailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartService {

    private final RestTemplate restTemplate;
    private final ProductService productService;

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final String PRODUCT_SERVICE_URL = "http://localhost:8082";


    public ShoppingCartService(RestTemplate restTemplate, ProductService productService) {
        this.restTemplate = restTemplate;
        this.productService = productService;
    }

    public void deleteShoppingCartItem(int itemId) {
        restTemplate.delete(PRODUCT_SERVICE_URL + "/items/" + itemId);
    }

    // ToDo: append user id from keycloak token for shopping cart service
    public List<ItemDetailDTO> fetchShoppingCartItems() throws ShoppingCartContentNotFoundException{
        List<ItemDetailDTO> listOfItems = new ArrayList<>();
        List<Integer> idsOfShoppingCartItems;

        ResponseEntity<Integer[]> response = restTemplate.getForEntity(PRODUCT_SERVICE_URL + "/items", Integer[].class);
        if(response.getStatusCode().is2xxSuccessful() && wereItemsFound(response)) {
            idsOfShoppingCartItems = List.of(response.getBody());
            listOfItems = fetchShoppingCartItemDetails(idsOfShoppingCartItems);
        }

        if(listOfItems.isEmpty())
            throw new ShoppingCartContentNotFoundException();
        return listOfItems;
    }

    private List<ItemDetailDTO> fetchShoppingCartItemDetails(List<Integer> idsOfShoppingCartItems) {
        List<ItemDetailDTO> listOfItems = new ArrayList<>();
        for(Integer id : idsOfShoppingCartItems) {
            try {
                listOfItems.add(productService.fetchSingleItem(id));
            } catch (NoItemsFoundException e) {
                logger.warn("Item with id: {} not found.", id);
                this.deleteNonExistingShoppingCartItem(id);
            } catch (Exception e) {
                logger.warn("Error when fetching details of shopping cart item from product service");
            }
        }
        return listOfItems;
    }

    private void deleteNonExistingShoppingCartItem(int i) {
        try {
            this.deleteShoppingCartItem(i);
        } catch (Exception e) {
            logger.warn("Shopping cart item could not be deleted ", e);
        }
    }

    private boolean wereItemsFound(ResponseEntity<Integer[]> response) {
        return response.getBody() != null && response.getBody().length > 0;
    }

    public void addShoppingCartItem(int itemId) {
        ResponseEntity<String> response = restTemplate.getForEntity(PRODUCT_SERVICE_URL + "/items", String.class);

        if(response.getStatusCode().is2xxSuccessful()) {
            logger.info("Item with id: {} successfully added to shopping cart", itemId);
        }else {
            logger.warn("Item with id: {} could not be added to shopping cart", itemId);
        }
    }
}
