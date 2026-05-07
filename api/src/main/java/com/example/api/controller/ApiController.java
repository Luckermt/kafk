package com.example.api.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.client.DataServiceClient;
import com.example.api.model.Order;
import com.example.api.service.KafkaProducerService;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    
    @Autowired
    private KafkaProducerService kafkaProducerService;
    
    @Autowired
    private DataServiceClient dataServiceClient;
    
    @PostMapping("/orders")
    public ResponseEntity<String> addOrder(@RequestBody Order order) {
        logger.info("Received request to create order: customerId={}, product={}", 
            order.getCustomerId(), order.getProductName());
        
        try {
            kafkaProducerService.sendOrder(order);
            return ResponseEntity.ok("Order sent to Kafka successfully");
        } catch (Exception e) {
            logger.error("Failed to send order to Kafka: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Failed to process order: " + e.getMessage());
        }
    }

    @GetMapping("/orders/search")
    public ResponseEntity<List<Map<String, Object>>> searchOrders(
            @RequestParam(required = false) String product,
            @RequestParam(required = false) String status) {
        
        logger.info("Searching orders: product={}, status={}", product, status);
        
        try {
            List<Map<String, Object>> orders = dataServiceClient.searchOrders(product, status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Failed to search orders: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/reports/sales-by-product")
    public ResponseEntity<List<Map<String, Object>>> getSalesByProduct() {
        logger.info("Fetching sales by product report");
        
        try {
            List<Map<String, Object>> report = dataServiceClient.getSalesByProduct();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            logger.error("Failed to get sales by product: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/reports/customer-spending")
    public ResponseEntity<List<Map<String, Object>>> getCustomerSpending() {
        logger.info("Fetching customer spending report");
        
        try {
            List<Map<String, Object>> report = dataServiceClient.getCustomerSpending();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            logger.error("Failed to get customer spending: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reports/daily-revenue")
    public ResponseEntity<List<Map<String, Object>>> getDailyRevenue() {
        logger.info("Fetching daily revenue report");
        
        try {
            List<Map<String, Object>> report = dataServiceClient.getDailyRevenue();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            logger.error("Failed to get daily revenue: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}