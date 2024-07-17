package com.springbootproject.springbootproject.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.lowagie.text.DocumentException;
import com.springbootproject.springbootproject.Entitities.Customer;

// Service interface for managing Customer entities
public interface CustomerService {

    // Method to add a new customer
    public Customer addCustomer(Customer customer);

    // Method to update an existing customer
    public Customer updateCustomer(Customer customer);

    // Method to delete a customer
    public Customer deleteCustomer(Long custId);

    // Method to retrieve all customers
    public List<Customer> getAllCustomers();

    // Method to retrieve a customer by ID
    public Customer getCustomerById(Long custId);

    // Method to generate a PDF of customer list
    public void generateCustomerPdf(List<Customer> customerList, OutputStream outputStream) throws DocumentException, IOException;

}
