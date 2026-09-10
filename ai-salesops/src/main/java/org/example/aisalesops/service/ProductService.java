package org.example.aisalesops.service;

import org.example.aisalesops.entity.Product;
import org.example.aisalesops.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;


    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    // Create Product
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }


    // Get All Products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    // Get Product By ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }


    // Full Update - PUT
    public Product updateProduct(Long id, Product updatedProduct) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + id
                        )
                );

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setModel(updatedProduct.getModel());
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setActive(updatedProduct.getActive());

        return productRepository.save(existingProduct);
    }


    // Partial Update - PATCH
    public Product partialUpdateProduct(
            Long id,
            Product updatedProduct
    ) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + id
                        )
                );


        if (updatedProduct.getName() != null) {
            existingProduct.setName(updatedProduct.getName());
        }


        if (updatedProduct.getModel() != null) {
            existingProduct.setModel(updatedProduct.getModel());
        }


        if (updatedProduct.getCategory() != null) {
            existingProduct.setCategory(updatedProduct.getCategory());
        }


        if (updatedProduct.getActive() != null) {
            existingProduct.setActive(updatedProduct.getActive());
        }


        return productRepository.save(existingProduct);
    }


    // Delete Product
    public void deleteProduct(Long id) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + id
                        )
                );

        productRepository.delete(existingProduct);
    }
}