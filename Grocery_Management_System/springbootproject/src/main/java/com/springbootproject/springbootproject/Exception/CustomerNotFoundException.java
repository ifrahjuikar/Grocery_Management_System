package com.springbootproject.springbootproject.Exception;

// Custom exception class for handling customer not found scenarios.
public class CustomerNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 7428051251365675318L;

    // Constructor with a message parameter to set the exception message.
    public CustomerNotFoundException(String message) {
        super(message); // Call to the superclass constructor to set the exception message.
    }
}
