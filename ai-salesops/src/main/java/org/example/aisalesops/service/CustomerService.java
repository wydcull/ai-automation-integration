package org.example.aisalesops.service;

import org.example.aisalesops.entity.Customer;
import org.example.aisalesops.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;


    public CustomerService(
            CustomerRepository customerRepository
    ) {
        this.customerRepository = customerRepository;
    }


    // Create Customer
    public Customer createCustomer(Customer customer) {

        return customerRepository.save(customer);
    }


    // Get All Customers
    public List<Customer> getAllCustomers() {

        return customerRepository.findAll();
    }


    // Get Customer By ID
    public Optional<Customer> getCustomerById(Long id) {

        return customerRepository.findById(id);
    }


    // Full Update - PUT
    public Customer updateCustomer(
            Long id,
            Customer updatedCustomer
    ) {

        Customer existingCustomer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found with id: " + id
                                )
                        );


        existingCustomer.setCompanyName(
                updatedCustomer.getCompanyName()
        );

        existingCustomer.setContactName(
                updatedCustomer.getContactName()
        );

        existingCustomer.setEmail(
                updatedCustomer.getEmail()
        );

        existingCustomer.setPhone(
                updatedCustomer.getPhone()
        );

        existingCustomer.setLocation(
                updatedCustomer.getLocation()
        );

        existingCustomer.setCountry(
                updatedCustomer.getCountry()
        );


        return customerRepository.save(existingCustomer);
    }


    // Partial Update - PATCH
    public Customer partialUpdateCustomer(
            Long id,
            Customer updatedCustomer
    ) {

        Customer existingCustomer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found with id: " + id
                                )
                        );


        if (updatedCustomer.getCompanyName() != null) {
            existingCustomer.setCompanyName(
                    updatedCustomer.getCompanyName()
            );
        }


        if (updatedCustomer.getContactName() != null) {
            existingCustomer.setContactName(
                    updatedCustomer.getContactName()
            );
        }


        if (updatedCustomer.getEmail() != null) {
            existingCustomer.setEmail(
                    updatedCustomer.getEmail()
            );
        }


        if (updatedCustomer.getPhone() != null) {
            existingCustomer.setPhone(
                    updatedCustomer.getPhone()
            );
        }


        if (updatedCustomer.getLocation() != null) {
            existingCustomer.setLocation(
                    updatedCustomer.getLocation()
            );
        }


        if (updatedCustomer.getCountry() != null) {
            existingCustomer.setCountry(
                    updatedCustomer.getCountry()
            );
        }


        return customerRepository.save(existingCustomer);
    }


    // Delete Customer
    public void deleteCustomer(Long id) {

        Customer existingCustomer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found with id: " + id
                                )
                        );


        customerRepository.delete(existingCustomer);
    }
}