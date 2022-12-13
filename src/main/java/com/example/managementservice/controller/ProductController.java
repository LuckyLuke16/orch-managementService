package com.example.managementservice.controller;

import com.example.managementservice.controller.controllerInterfaces.ProductOperations;
import com.example.managementservice.model.Genre;
import com.example.managementservice.model.ItemDTO;
import com.example.managementservice.model.ItemDetailDTO;
import com.example.managementservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class ProductController implements ProductOperations {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public List<ItemDTO> fetchAllItems(String genre) {
        List<ItemDTO> allItemsList;
        try {
            Genre genreEnum = Genre.valueOf(genre);
            allItemsList = productService.fetchAllProducts(genreEnum);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return allItemsList;
    }

    public ItemDetailDTO fetchSingleItem(int itemID) {
        ItemDetailDTO singleItemToFetch;
        try {
            singleItemToFetch = productService.fetchSingleItem(itemID);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return singleItemToFetch;
    }
}
