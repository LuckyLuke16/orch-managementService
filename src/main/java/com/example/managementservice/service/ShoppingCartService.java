package com.example.managementservice.service;

import com.example.managementservice.exception.NoItemsFoundException;
import com.example.managementservice.exception.ShoppinCartItemDeletionFailedException;
import com.example.managementservice.exception.ShoppingCartContentNotFoundException;
import com.example.managementservice.model.ItemDetailDTO;
import com.example.managementservice.model.ShoppingCartItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ShoppingCartService {

    private final RestTemplate restTemplate;
    private final ProductService productService;

    Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);

    private final String SHOPPING_CART_SERVICE_URL = "http://localhost:8082";

    public ShoppingCartService(RestTemplate restTemplate, ProductService productService) {
        this.restTemplate = restTemplate;
        this.productService = productService;
    }

    public void deleteShoppingCartItem(int itemId, String userId) {
        restTemplate.delete(SHOPPING_CART_SERVICE_URL + "/shopping-cart/" + itemId + "?user=");
    }

    public List<ItemDetailDTO> fetchShoppingCartItems(String userId) throws ShoppingCartContentNotFoundException{
        List<ItemDetailDTO> listOfItems = new ArrayList<>();
        Set<Integer> idsOfShoppingCartItems;

        ResponseEntity<ShoppingCartItemDTO> response = restTemplate.getForEntity(SHOPPING_CART_SERVICE_URL + "/shopping-cart?user=" + userId, ShoppingCartItemDTO.class);
        if(response.getStatusCode().is2xxSuccessful() && wereItemsFound(response)) {
            idsOfShoppingCartItems = response.getBody().getItemsFromShoppingCart().keySet();
            listOfItems = fetchShoppingCartItemDetails(idsOfShoppingCartItems, userId);
        }

        if(listOfItems.isEmpty())
            throw new ShoppingCartContentNotFoundException();
        return listOfItems;
    }

    private List<ItemDetailDTO> fetchShoppingCartItemDetails(Set<Integer> idsOfShoppingCartItems, String userId) {
        List<ItemDetailDTO> listOfItems = new ArrayList<>();
        for(Integer id : idsOfShoppingCartItems) {
            try {
                listOfItems.add(productService.fetchSingleItem(id));
            } catch (NoItemsFoundException e) {
                logger.warn("Item with id: {} not found.", id);
                this.deleteNonExistingShoppingCartItem(id, userId);
            } catch (Exception e) {
                logger.warn("Error when fetching details of shopping cart item from product service");
            }
        }
        return listOfItems;
    }

    private void deleteNonExistingShoppingCartItem(int i, String userId) {
        try {
            this.deleteShoppingCartItem(i, userId);
        } catch (Exception e) {
            logger.warn("Shopping cart item could not be deleted ", e);
        }
    }

    private boolean wereItemsFound(ResponseEntity<ShoppingCartItemDTO> response) {
        return response.getBody() != null && response.getBody().getItemsFromShoppingCart() != null && !response.getBody().getItemsFromShoppingCart().isEmpty();
    }

    public void addShoppingCartItem(int itemId, String userId) {
        ResponseEntity<String> response = restTemplate.getForEntity(SHOPPING_CART_SERVICE_URL + "/items" + "/shopping-cart?user=" + userId, String.class);

        if(response.getStatusCode().is2xxSuccessful()) {
            logger.info("Item with id: {} successfully added to shopping cart", itemId);
        }else {
            logger.warn("Item with id: {} could not be added to shopping cart", itemId);
            throw new ShoppinCartItemDeletionFailedException(itemId);
        }
    }
}
