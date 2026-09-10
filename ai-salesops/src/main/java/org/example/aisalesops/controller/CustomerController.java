package org.example.aisalesops.controller;

import org.example.aisalesops.entity.Customer;
import org.example.aisalesops.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;


    public CustomerController(
            CustomerService customerService
    ) {
        this.customerService = customerService;
    }


    // Create Customer
    @PostMapping
    public Customer createCustomer(
            @RequestBody Customer customer
    ) {

        return customerService.createCustomer(customer);
    }


    // Get All Customers
    @GetMapping
    public List<Customer> getAllCustomers() {

        return customerService.getAllCustomers();
    }


    // Get Customer By ID
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(
            @PathVariable Long id
    ) {

        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Full Update - PUT
    @PutMapping("/{id}")
    public Customer updateCustomer(
            @PathVariable Long id,
            @RequestBody Customer customer
    ) {

        return customerService.updateCustomer(
                id,
                customer
        );
    }


    // Partial Update - PATCH
    @PatchMapping("/{id}")
    public Customer partialUpdateCustomer(
            @PathVariable Long id,
            @RequestBody Customer customer
    ) {

        return customerService.partialUpdateCustomer(
                id,
                customer
        );
    }


    // Delete Customer
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(
            @PathVariable Long id
    ) {

        customerService.deleteCustomer(id);

        return ResponseEntity.ok(
                "Customer deleted successfully"
        );
    }
}