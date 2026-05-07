package com.example.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.api.model.Order;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${data.service.url}")
    private String dataServiceUrl;
    
    private static final String TOPIC = "orders-topic";
    
    @PostMapping("/orders")
    public ResponseEntity<String> addOrder(@RequestBody Order order) {
        kafkaTemplate.send(TOPIC, order);
        return ResponseEntity.ok("Order sent to Kafka successfully");
    }
    
    @GetMapping("/orders/search")
    public ResponseEntity<Object> searchOrders(@RequestParam(required = false) String product,
                                                @RequestParam(required = false) String status) {
        String url = dataServiceUrl + "/api/data/orders/search";
        if (product != null) url += "?product=" + product;
        if (status != null) url += (url.contains("?") ? "&" : "?") + "status=" + status;
        
        Object result = restTemplate.getForObject(url, Object.class);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/reports/sales-by-product")
    public ResponseEntity<Object> getSalesByProduct() {
        String url = dataServiceUrl + "/api/data/reports/sales-by-product";
        Object result = restTemplate.getForObject(url, Object.class);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/reports/customer-spending")
    public ResponseEntity<Object> getCustomerSpending() {
        String url = dataServiceUrl + "/api/data/reports/customer-spending";
        Object result = restTemplate.getForObject(url, Object.class);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/reports/daily-revenue")
    public ResponseEntity<Object> getDailyRevenue() {
        String url = dataServiceUrl + "/api/data/reports/daily-revenue";
        Object result = restTemplate.getForObject(url, Object.class);
        return ResponseEntity.ok(result);
    }
}