package com.example.managementservice.service;

import com.example.managementservice.exception.PaymentFailedException;
import com.example.managementservice.model.ShoppingCartItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PaymentService {

    private final RestTemplate restTemplate;

    Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final String PRODUCT_SERVICE_URL = "http://localhost:8083";


    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public long makePayment(String paymentMethod, String userId, List<ShoppingCartItemDTO> itemsToPayFor) {
        ResponseEntity<Long> response = restTemplate.postForEntity(PRODUCT_SERVICE_URL + "/payments"
                + "?userId=" + userId + "&paymentMethod=" + paymentMethod, itemsToPayFor, Long.class);

        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            logger.info("Payment with payment id: {} was successful", response.getBody());
            return response.getBody();
        }
        throw new PaymentFailedException();
    }
}
