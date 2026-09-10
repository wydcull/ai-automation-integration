package org.example.aisalesops.repository;

import org.example.aisalesops.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}