package com.example.managementservice.service;

import com.example.managementservice.model.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductService {

    private final RestTemplate restTemplate;

    private final String productServiceURL = "http://";

    @Autowired
    public ProductService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ItemDTO> fetchAllProducts() {

        return null;
    }
}
