package com.springbootproject.springbootproject.Service;

import java.util.Map;

import com.springbootproject.springbootproject.Entitities.Bill;

// Service interface for managing Bill entities
public interface BillService {
    
    // Method to generate a bill
    public Map<String, Object> generateBill(Bill billRequest);

}
