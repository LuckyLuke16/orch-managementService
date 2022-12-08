package com.example.managementservice.controller;

import com.example.managementservice.model.ItemDTO;
import com.example.managementservice.model.ItemDetailDTO;
import com.example.managementservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductController implements ProductOperations{

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public List<ItemDTO> fetchAllItems() {
        List<ItemDTO> allItemsList = new ArrayList<>();

        allItemsList = productService.fetchAllProducts();

        return null;
    }

    @Override
    public ItemDetailDTO fetchSingleItem(String itemID) {
        return null;
    }
}
