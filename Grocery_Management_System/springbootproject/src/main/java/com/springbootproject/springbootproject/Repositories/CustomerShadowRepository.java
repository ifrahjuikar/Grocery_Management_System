package com.springbootproject.springbootproject.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springbootproject.springbootproject.Entitities.CustomerShadow;

public interface CustomerShadowRepository extends JpaRepository<CustomerShadow, String>{
  
}
