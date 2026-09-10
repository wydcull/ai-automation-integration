package org.example.aisalesops.repository;

import org.example.aisalesops.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
        SELECT COUNT(p)
        FROM Product p
        WHERE p.active = true
    """)
    long countActiveProducts();

    long countByActive(Boolean active);
}