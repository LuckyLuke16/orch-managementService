package com.example.managementservice.service;

import com.example.managementservice.exception.NoItemsFoundException;
import com.example.managementservice.exception.StockUpdateException;
import com.example.managementservice.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Service
public class ProductService {

    private final RestTemplate restTemplate;

    Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final String PRODUCT_SERVICE_URL = "http://localhost:8081";

    public ProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ItemDTO> fetchAllProducts(Genre genre) throws NoItemsFoundException {
        List<ItemDTO> listOfAllProducts;

        ResponseEntity<ItemDTO[]> response = restTemplate.getForEntity(PRODUCT_SERVICE_URL + "/items" + "?genre=" + genre, ItemDTO[].class);
        if (response.getStatusCode().is2xxSuccessful() && wereItemsFound(response)) {
            listOfAllProducts = List.of(response.getBody());
            logger.info("{} items found", listOfAllProducts.size());
            return listOfAllProducts;
        }
        logger.warn("No items could be fetch {}", response.getStatusCode());
        throw new NoItemsFoundException();
    }

    private boolean wereItemsFound(ResponseEntity<ItemDTO[]> response) {
        return response.getBody() != null && response.getBody().length > 0;
    }

    public ItemDetailDTO fetchSingleItem(int itemID) {
        ItemDetailDTO singleItemToFetch;
        try {
            ResponseEntity<ItemDetailDTO> response = restTemplate
                .getForEntity(PRODUCT_SERVICE_URL + "/items" + "/" + itemID, ItemDetailDTO.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                singleItemToFetch = (response.getBody());
                return singleItemToFetch;
            }
        } catch (Exception e) {
            logger.warn("Single Item with id: {} could not be fetched", itemID);
        }
        throw new NoItemsFoundException();
    }

    public void orderItems(List<ShoppingCartItemDTO> itemsFromShoppingCart) {
        ItemQuantityDTO itemsToOrder = new ItemQuantityDTO();
        itemsToOrder.setItemsFromShoppingCart(toItemIdWithQuantityMap(itemsFromShoppingCart));
        ResponseEntity<List> response = restTemplate.postForEntity(PRODUCT_SERVICE_URL + "/items" + "/stock", itemsToOrder, List.class);

        if (response.getBody().isEmpty()) {
            logger.info("Item stock was successfully updated");
            return;
        }
        logger.warn("No items could be fetch {}", response.getStatusCode());
        throw new StockUpdateException();
    }

    private HashMap<Integer, Integer> toItemIdWithQuantityMap(List<ShoppingCartItemDTO> itemsFromShoppingCart) {
        HashMap<Integer, Integer> mapWithIdsAndQuantity = new HashMap<>();
        for (ShoppingCartItemDTO item : itemsFromShoppingCart) {
            mapWithIdsAndQuantity.put(item.getId(), item.getQuantityInCart());
        }
        return mapWithIdsAndQuantity;
    }

    public void resetStock(List<ShoppingCartItemDTO> itemsToReset) {
        ItemQuantityDTO itemQuantityDTO = new ItemQuantityDTO();
        itemQuantityDTO.setItemsFromShoppingCart(toItemIdWithQuantityMap(itemsToReset));

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(PRODUCT_SERVICE_URL + "/items/stock/reset", itemQuantityDTO, String.class);
            logger.info("Stock of items was successfully reset: {}", response.getBody());
        } catch (Exception e) {
            logger.warn("Stock of items could not be reset");
            throw new StockUpdateException();
        }
    }
}
