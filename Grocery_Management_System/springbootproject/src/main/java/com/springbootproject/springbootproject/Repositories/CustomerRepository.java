package com.springbootproject.springbootproject.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springbootproject.springbootproject.Entitities.Customer;

// Repository interface for managing Customer entities
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // Custom method to find a customer by full name
    Optional<Customer> findByFullName(String name);
}
