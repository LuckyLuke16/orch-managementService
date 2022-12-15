package com.example.managementservice.controller;

import com.example.managementservice.controller.controllerInterfaces.ProductOperations;
import com.example.managementservice.model.Genre;
import com.example.managementservice.model.ItemDTO;
import com.example.managementservice.model.ItemDetailDTO;
import com.example.managementservice.service.ProductService;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@RestController
public class ProductController implements ProductOperations {

    @Autowired
    private HttpServletRequest request;
    Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;


    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public List<ItemDTO> fetchItemsByCategory(String genre) {
        List<ItemDTO> allItemsList;
        try {
            Genre genreEnum = Genre.valueOf(genre);
            allItemsList = productService.fetchAllProducts(genreEnum);
        } catch (Exception e) {
            logger.warn("Items could not be fetched", e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return allItemsList;
    }

    public ItemDetailDTO fetchSingleItem(int itemID) {
        Principal user = this.request.getUserPrincipal();
        KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Principal principal = (Principal) authentication.getPrincipal();
        String userId = ((KeycloakPrincipal) principal).getKeycloakSecurityContext().getIdToken().getSubject();
        ItemDetailDTO singleItemToFetch;
        try {
            singleItemToFetch = productService.fetchSingleItem(itemID);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return singleItemToFetch;
    }
}
