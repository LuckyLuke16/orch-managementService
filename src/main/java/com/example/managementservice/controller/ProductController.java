package com.example.managementservice.controller;

import com.example.managementservice.model.ItemDTO;
import com.example.managementservice.model.ItemDetailDTO;
import com.example.managementservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class ProductController implements ProductOperations{

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public List<ItemDTO> fetchAllItems() {
        List<ItemDTO> allItemsList;

        try {
            allItemsList = productService.fetchAllProducts();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return allItemsList;
    }

    public ItemDetailDTO fetchSingleItem(String itemID) {
        return null;
    }
}
