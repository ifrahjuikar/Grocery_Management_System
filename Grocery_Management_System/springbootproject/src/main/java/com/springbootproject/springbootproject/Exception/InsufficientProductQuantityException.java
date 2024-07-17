package com.springbootproject.springbootproject.Exception;

// Custom exception class for handling insufficient product quantity scenarios.
public class InsufficientProductQuantityException extends RuntimeException {

    // Constructor with a message parameter to set the exception message.
    public InsufficientProductQuantityException(String message) {
        super(message); // Call to the superclass constructor to set the exception message.
    }
}
