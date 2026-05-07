package com.example.api.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Component
public class DataServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(DataServiceClient.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${data.service.url}")
    private String dataServiceUrl;

    public List<Map<String, Object>> searchOrders(String product, String status) {
        String url = dataServiceUrl + "/api/data/orders/search";
        
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (product != null && !product.isEmpty()) {
            builder.queryParam("product", product);
        }
        if (status != null && !status.isEmpty()) {
            builder.queryParam("status", status);
        }
        
        String finalUrl = builder.toUriString();
        logger.info("Fetching orders from Data Service: url={}", finalUrl);
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                finalUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            logger.info("Successfully fetched {} orders", 
                response.getBody() != null ? response.getBody().size() : 0);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Failed to fetch orders: url={}, error={}", finalUrl, e.getMessage());
            throw new RuntimeException("Failed to fetch orders from Data Service", e);
        }
    }

    public List<Map<String, Object>> getSalesByProduct() {
        String url = dataServiceUrl + "/api/data/reports/sales-by-product";
        logger.info("Fetching sales by product report: url={}", url);
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            logger.info("Successfully fetched sales by product report");
            return response.getBody();
        } catch (Exception e) {
            logger.error("Failed to fetch sales by product report: error={}", e.getMessage());
            throw new RuntimeException("Failed to fetch sales report", e);
        }
    }

    public List<Map<String, Object>> getCustomerSpending() {
        String url = dataServiceUrl + "/api/data/reports/customer-spending";
        logger.info("Fetching customer spending report: url={}", url);
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            logger.info("Successfully fetched customer spending report");
            return response.getBody();
        } catch (Exception e) {
            logger.error("Failed to fetch customer spending report: error={}", e.getMessage());
            throw new RuntimeException("Failed to fetch customer spending report", e);
        }
    }

    public List<Map<String, Object>> getDailyRevenue() {
        String url = dataServiceUrl + "/api/data/reports/daily-revenue";
        logger.info("Fetching daily revenue report: url={}", url);
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            logger.info("Successfully fetched daily revenue report");
            return response.getBody();
        } catch (Exception e) {
            logger.error("Failed to fetch daily revenue report: error={}", e.getMessage());
            throw new RuntimeException("Failed to fetch daily revenue report", e);
        }
    }
}