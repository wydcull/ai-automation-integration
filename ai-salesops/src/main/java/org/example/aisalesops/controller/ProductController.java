package org.example.aisalesops.controller;

import org.example.aisalesops.entity.Product;
import org.example.aisalesops.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    // Create Product
    @PostMapping
    public Product createProduct(
            @RequestBody Product product
    ) {
        return productService.createProduct(product);
    }


    // Get All Products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Count Products
    @GetMapping("/count")
    public ResponseEntity<Long> countProducts(
            @RequestParam(required = false) Boolean active
    ) {
        return ResponseEntity.ok(
                productService.countProducts(active)
        );
    }


    // Get Product By ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id
    ) {

        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Full Update
    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product
    ) {

        return productService.updateProduct(id, product);
    }


    // Partial Update
    @PatchMapping("/{id}")
    public Product partialUpdateProduct(
            @PathVariable Long id,
            @RequestBody Product product
    ) {

        return productService.partialUpdateProduct(id, product);
    }


    // Delete Product
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id
    ) {

        productService.deleteProduct(id);

        return ResponseEntity.ok(
                "Product deleted successfully"
        );
    }
}