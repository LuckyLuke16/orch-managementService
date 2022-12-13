package com.example.managementservice.service;

import com.example.managementservice.exception.ShoppingCartContentNotFoundException;
import com.example.managementservice.model.ItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ShoppingCartService {

    private final RestTemplate restTemplate;

    private final String PRODUCT_SERVICE_URL = "http://localhost:8082";

    public ShoppingCartService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void deleteShoppingCartItem(int itemId) {
        restTemplate.delete(PRODUCT_SERVICE_URL + "/items/" + itemId);
    }

    public List<ItemDTO> fetchShoppingCartItems() throws ShoppingCartContentNotFoundException{
        List<ItemDTO> listOfAllProducts;

        ResponseEntity<ItemDTO[]> response = restTemplate.getForEntity(PRODUCT_SERVICE_URL + "/items", ItemDTO[].class);
        if(response.getStatusCode().is2xxSuccessful() && wereItemsFound(response)) {
            listOfAllProducts = List.of(response.getBody());

            return listOfAllProducts;
        }
        throw new ShoppingCartContentNotFoundException();
    }

    private boolean wereItemsFound(ResponseEntity<ItemDTO[]> response) {
        return response.getBody() != null && response.getBody().length > 0;
    }
}
