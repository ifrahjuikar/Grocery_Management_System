
package com.springbootproject.springbootproject.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.springbootproject.springbootproject.utility.LoggingErrorUtility;

import org.springframework.http.converter.HttpMessageNotReadableException;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class GlobalExceptionHandler {

        // Handles HttpMessageNotReadableException, typically thrown when the JSON
        // request is malformed.
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                        WebRequest request) {
                LoggingErrorUtility.errorLogging("http.message.not.readable.exception");
                ApiResponse response = new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(),
                                "Malformed JSON request", null);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Handles DataIntegrityViolationException, usually thrown when there is a
        // database constraint violation.
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                        WebRequest request) {
                LoggingErrorUtility.errorLogging("data.integrity.violation.exception");
                ApiResponse response = new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(),
                                "Data integrity violation",
                                null);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Handles NoHandlerFoundException, typically thrown when there's no handler
        // found for a URI.
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ApiResponse> handleNoHandlerFound(NoHandlerFoundException ex, WebRequest request) {
                // Log the error
                LoggingErrorUtility.errorLogging("no.handler.found.exception");

                // Create an error response
                ApiResponse response = new ApiResponse("Error", HttpStatus.NOT_FOUND.value(),
                                "No handler found for this URI", null);
                // Return the error response with a 404 Not Found status
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Handles MethodArgumentNotValidException, which is thrown when validation on
        // an argument annotated with @Valid fails.
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                        WebRequest request) {
                LoggingErrorUtility.errorLogging("method.argument.not.valid");
                ApiResponse response = new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(),
                                "Validation error: " + ex.getBindingResult().toString(), null);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Handles exception when customer is not found
        @ExceptionHandler(CustomerNotFoundException.class)
        public ResponseEntity<ApiResponse> handleCustomerNotFoundException(CustomerNotFoundException ex,
                        WebRequest request) {
                LoggingErrorUtility.errorLogging("customer.not.found.exception", ex.getMessage());
                ApiResponse response = new ApiResponse("Error", HttpStatus.NOT_FOUND.value(),
                                "Customer not found: " + ex.getMessage(), null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Handles exception when product is not found
        @ExceptionHandler(ProductNotFoundException.class)
        public ResponseEntity<ApiResponse> handleProductNotFoundException(ProductNotFoundException ex,
                        WebRequest request) {
                LoggingErrorUtility.errorLogging("product.not.found.exception", ex.getMessage());
                ApiResponse response = new ApiResponse("Error", HttpStatus.NOT_FOUND.value(),
                                "Product not found: " + ex.getMessage(), null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Handles exception when insufficient product exception 
        @ExceptionHandler(InsufficientProductQuantityException.class)
        public ResponseEntity<ApiResponse> handleInsufficientProductQuantityException(InsufficientProductQuantityException ex,
                        WebRequest request) {
                LoggingErrorUtility.errorLogging("insufficient.product.quantity.exception", ex.getMessage());
                ApiResponse response = new ApiResponse("Error", HttpStatus.NOT_FOUND.value(),
                                "Insufficient: " + ex.getMessage(), null);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Handles HttpRequestMethodNotSupportedException, thrown when an unsupported
        // HTTP method is used.
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ApiResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
                        WebRequest request) {
                LoggingErrorUtility.errorLogging("method.not.allowed", ex.getMessage());
                ApiResponse response = new ApiResponse("Error", HttpStatus.METHOD_NOT_ALLOWED.value(),
                                "Unsupported HTTP method used: " + ex.getMethod(), null);
                return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
        }

       
        // Handles all other exceptions that are not explicitly handled by other
        // methods.
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse> handleAllExceptions(Exception ex, WebRequest request) {
                LoggingErrorUtility.errorLogging("handle.all.exceptions");
                ApiResponse response = new ApiResponse("Error", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Internal server error", null);
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

}
