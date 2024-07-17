package com.springbootproject.springbootproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springbootproject.springbootproject.Entitities.Product;

// Repository interface for managing Product entities
public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
