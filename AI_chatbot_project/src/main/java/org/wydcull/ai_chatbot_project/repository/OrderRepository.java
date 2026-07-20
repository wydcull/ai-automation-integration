package org.wydcull.ai_chatbot_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wydcull.ai_chatbot_project.model.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerEmail(String email);
    Optional<Order> findByTrackingNumber(String trackingNumber);
}