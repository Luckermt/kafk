package com.example.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.data.entity.Customer;
import com.example.data.entity.Order;
import com.example.data.repository.CustomerRepository;
import com.example.data.repository.OrderRepository;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Transactional
    public void saveOrder(com.example.data.model.OrderMessage orderMessage) {
        Customer customer = customerRepository.findById(orderMessage.getCustomerId())
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + orderMessage.getCustomerId()));
        
        Order order = new Order();
        order.setCustomer(customer);
        order.setProductName(orderMessage.getProductName());
        order.setQuantity(orderMessage.getQuantity());
        order.setPrice(orderMessage.getPrice());
        order.setStatus(orderMessage.getStatus() != null ? orderMessage.getStatus() : "PENDING");
        order.setOrderDate(java.time.LocalDateTime.now());
        
        orderRepository.save(order);
    }
    
    public List<Order> searchOrders(String product, String status) {
        if (product != null && !product.isEmpty() && status != null && !status.isEmpty()) {
            return orderRepository.findByProductNameContainingIgnoreCaseAndStatus(product, status);
        } else if (product != null && !product.isEmpty()) {
            return orderRepository.findByProductNameContainingIgnoreCase(product);
        } else if (status != null && !status.isEmpty()) {
            return orderRepository.findByStatus(status);
        } else {
            return orderRepository.findAll();
        }
    }
    
    public List<Map<String, Object>> getSalesByProduct() {
        return orderRepository.getSalesByProduct();
    }
    
    public List<Map<String, Object>> getCustomerSpending() {
        return orderRepository.getCustomerSpending();
    }
    
    public List<Map<String, Object>> getDailyRevenue() {
        return orderRepository.getDailyRevenue();
    }
}