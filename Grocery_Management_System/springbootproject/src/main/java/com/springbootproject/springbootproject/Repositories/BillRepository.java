package com.springbootproject.springbootproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springbootproject.springbootproject.Entitities.Bill;

// Repository interface for managing Bill entities
public interface BillRepository extends JpaRepository<Bill, Long> {
    
}
 