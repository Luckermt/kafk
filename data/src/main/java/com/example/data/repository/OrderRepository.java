package com.example.data.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.data.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    List<Order> findByProductNameContainingIgnoreCase(String productName);
    
    List<Order> findByStatus(String status);
    
    List<Order> findByProductNameContainingIgnoreCaseAndStatus(String productName, String status);
    
    @Query("SELECT o.productName as product, SUM(o.quantity * o.price) as totalRevenue " +
           "FROM Order o GROUP BY o.productName ORDER BY totalRevenue DESC")
    List<Map<String, Object>> getSalesByProduct();
    
    @Query("SELECT c.name as customerName, c.email as email, " +
           "SUM(o.quantity * o.price) as totalSpent, COUNT(o) as orderCount " +
           "FROM Order o JOIN o.customer c GROUP BY c.name, c.email ORDER BY totalSpent DESC")
    List<Map<String, Object>> getCustomerSpending();
    
    @Query("SELECT DATE(o.orderDate) as date, COUNT(o) as orderCount, " +
           "SUM(o.quantity * o.price) as revenue " +
           "FROM Order o GROUP BY DATE(o.orderDate) ORDER BY date DESC")
    List<Map<String, Object>> getDailyRevenue();
}