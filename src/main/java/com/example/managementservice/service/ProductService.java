package com.example.managementservice.service;

import com.example.managementservice.exception.NoItemsFoundException;
import com.example.managementservice.model.ItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductService {

    private final RestTemplate restTemplate;

    private final String PRODUCT_SERVICE_URL = "http://localhost:8081";

    public ProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ItemDTO> fetchAllProducts() throws NoItemsFoundException{
        List<ItemDTO> listOfAllProducts;

        ResponseEntity<ItemDTO[]> response = restTemplate.getForEntity(PRODUCT_SERVICE_URL + "/items", ItemDTO[].class);
        if(response.getStatusCode().is2xxSuccessful() && areItemsFound(response)) {
            listOfAllProducts = List.of(response.getBody());

            return listOfAllProducts;
        }
        throw new NoItemsFoundException();
    }

    private boolean areItemsFound(ResponseEntity<ItemDTO[]> response) {
        return response.getBody() != null && response.getBody().length > 0;
    }
}
