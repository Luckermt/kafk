package com.example.api.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.example.api.model.Order;

@Service
public class KafkaProducerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${kafka.topic.orders}")
    private String ordersTopic;
    
    public CompletableFuture<SendResult<String, Object>> sendOrder(Order order) {
        String partitionKey = String.valueOf(order.getCustomerId());
        
        logger.info("Sending order to Kafka: topic={}, key={}, product={}", 
            ordersTopic, partitionKey, order.getProductName());
        
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send(ordersTopic, partitionKey, order);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Order sent successfully: key={}, partition={}, offset={}, product={}", 
                    partitionKey, 
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    order.getProductName());
            } else {
                logger.error("Failed to send order: key={}, product={}, error={}", 
                    partitionKey, order.getProductName(), ex.getMessage());
            }
        });
        
        return future;
    }
    
    public SendResult<String, Object> sendOrderSync(Order order) throws Exception {
        String partitionKey = String.valueOf(order.getCustomerId());
        
        logger.info("Sending order synchronously to Kafka: topic={}, key={}", 
            ordersTopic, partitionKey);
        
        SendResult<String, Object> result = 
            kafkaTemplate.send(ordersTopic, partitionKey, order).get();
        
        logger.info("Order sent synchronously: partition={}, offset={}", 
            result.getRecordMetadata().partition(),
            result.getRecordMetadata().offset());
        
        return result;
    }
}
