package com.example.data.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.data.entity.Order;
import com.example.data.service.OrderService;

@RestController
@RequestMapping("/api/data")
public class DataController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping("/orders/search")
    public ResponseEntity<List<Order>> searchOrders(
            @RequestParam(required = false) String product,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderService.searchOrders(product, status));
    }
    
    @GetMapping("/reports/sales-by-product")
    public ResponseEntity<List<Map<String, Object>>> getSalesByProduct() {
        return ResponseEntity.ok(orderService.getSalesByProduct());
    }
    
    @GetMapping("/reports/customer-spending")
    public ResponseEntity<List<Map<String, Object>>> getCustomerSpending() {
        return ResponseEntity.ok(orderService.getCustomerSpending());
    }
    
    @GetMapping("/reports/daily-revenue")
    public ResponseEntity<List<Map<String, Object>>> getDailyRevenue() {
        return ResponseEntity.ok(orderService.getDailyRevenue());
    }
}