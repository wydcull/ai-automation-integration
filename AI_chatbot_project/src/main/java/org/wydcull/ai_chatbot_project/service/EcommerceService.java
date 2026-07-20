package org.wydcull.ai_chatbot_project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wydcull.ai_chatbot_project.model.Order;
import org.wydcull.ai_chatbot_project.model.Product;
import org.wydcull.ai_chatbot_project.repository.OrderRepository;
import org.wydcull.ai_chatbot_project.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EcommerceService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public Optional<Product> findProductByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
    }

    public List<Product> findProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Optional<Order> findOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    public Optional<Order> findOrderByTracking(String trackingNumber) {
        return orderRepository.findByTrackingNumber(trackingNumber);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getInStockProducts() {
        return productRepository.findByInStockTrue();
    }
}