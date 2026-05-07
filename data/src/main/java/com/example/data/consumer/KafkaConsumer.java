package com.example.data.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.data.model.OrderMessage;
import com.example.data.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaConsumer {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @KafkaListener(topics = "orders-topic", groupId = "data-service-group")
    public void consume(String message) {
        try {
            OrderMessage orderMessage = objectMapper.readValue(message, OrderMessage.class);
            orderService.saveOrder(orderMessage);
            System.out.println("Order saved successfully: " + orderMessage.getProductName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}