package com.springbootproject.springbootproject.Exception;

// Custom exception class for handling product not found scenarios.
public class ProductNotFoundException extends RuntimeException {

    // Constructor with a message parameter to set the exception message.
    public ProductNotFoundException(String message) {
        super(message); // Call to the superclass constructor to set the exception message.
    }
}
