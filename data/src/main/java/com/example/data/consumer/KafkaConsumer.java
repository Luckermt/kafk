package com.example.data.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
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
    public void consume(ConsumerRecord<String, String> record) {
        try {
            String key = record.key();
            String message = record.value();
            int partition = record.partition();
            
            System.out.println("Received message with key: " + key + 
                " from partition: " + partition);
            
            OrderMessage orderMessage = objectMapper.readValue(message, OrderMessage.class);
            orderService.saveOrder(orderMessage);
            System.out.println("Order saved successfully: " + orderMessage.getProductName() + 
                " for customer: " + key);
        } catch (Exception e) {
            System.err.println("Error processing message from partition " + 
                record.partition() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}