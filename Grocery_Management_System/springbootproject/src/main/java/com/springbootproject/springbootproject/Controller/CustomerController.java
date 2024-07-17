package com.springbootproject.springbootproject.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lowagie.text.DocumentException;
import com.springbootproject.springbootproject.Entitities.Customer;
import com.springbootproject.springbootproject.Exception.ApiResponse;
import com.springbootproject.springbootproject.Service.CustomerService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller class for managing customer-related endpoints.
 */
@RestController
public class CustomerController {

        private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

        @Autowired
        private CustomerService customerService;

        /**
         * Adds a new customer.
         *
         * @param customer the customer object containing customer details.
         * @return ResponseEntity containing the API response with customer details.
         */
        @PostMapping("/customer")
        public ResponseEntity<?> addCustomer(@RequestBody Customer customer) {
                log.debug("Received request to add customer: {}", customer);
                ResponseEntity<?> responseEntity;
                // Check if request body is empty
                if (isRequestBodyEmpty(customer)) {
                        log.warn("Request body is empty: {}", customer);
                        responseEntity = new ResponseEntity<>(new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(),
                                        "Request Body is Empty", null), HttpStatus.BAD_REQUEST);
                } else {
                        // Add customer to database
                        customerService.addCustomer(customer);
                        log.info("Customer added successfully: {}", customer);
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(), "Customer added successfully",
                                                        customer),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Updates an existing customer.
         *
         * @param customer the customer object containing updated customer details.
         * @return ResponseEntity containing the API response with updated customer
         *         details.
         */
        @PutMapping("/customer")
        public ResponseEntity<?> updateCustomer(@RequestBody Customer customer) {
                log.debug("Received request to update customer: {}", customer);
                ResponseEntity<?> responseEntity;

                // Check if request body is empty
                if (isRequestBodyEmpty(customer)) {
                        log.warn("Request body is empty: {}", customer);
                        responseEntity = new ResponseEntity<>(new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(),
                                        "Request Body is Empty", null), HttpStatus.BAD_REQUEST);
                } else {
                        // Update customer
                        Customer updatedCustomer = customerService.updateCustomer(customer);
                        Long customerId = customer.getCustomerId();

                        log.info("Customer with ID {} updated successfully: {}", customerId, updatedCustomer);
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(),
                                                        "Customer with ID " + customerId + " updated successfully",
                                                        updatedCustomer),
                                        HttpStatus.OK);
                }

                return responseEntity;
        }

        /**
         * Deletes a customer by ID.
         *
         * @param custId the ID of the customer to be deleted.
         * @return ResponseEntity containing the API response.
         */
        @DeleteMapping("/customer/{custId}")
        public ResponseEntity<?> deleteCustomer(@PathVariable Long custId) {
                log.debug("Received request to delete customer with ID: {}", custId);
                ResponseEntity<?> responseEntity;

                // Delete customer
                Customer customer = customerService.deleteCustomer(custId);
                log.info("Customer deleted successfully with ID: {}", custId);

                responseEntity = new ResponseEntity<>(
                                new ApiResponse("Success", HttpStatus.OK.value(), "Customer deleted successfully",
                                                customer),
                                HttpStatus.OK);

                return responseEntity;
        }

        /**
         * Retrieves all customers.
         *
         * @return ResponseEntity containing the API response with the list of
         *         customers.
         */
        @GetMapping("/customers")
        public ResponseEntity<?> getAllCustomers() {
                log.debug("Received request to retrieve all customers");
                ResponseEntity<?> responseEntity;

                // Retrieve all customers
                List<Customer> customers = customerService.getAllCustomers();
                log.info("Customers retrieved successfully");

                responseEntity = new ResponseEntity<>(
                                new ApiResponse("Success", HttpStatus.OK.value(), "Retrieved all customers", customers),
                                HttpStatus.OK);

                return responseEntity;
        }

        /**
         * Retrieves a customer by ID.
         *
         * @param custId the ID of the customer to be retrieved.
         * @return ResponseEntity containing the API response with the customer details.
         */
        @GetMapping("/customer/{custId}")
        public ResponseEntity<?> getCustomerById(@PathVariable Long custId) {
                log.debug("Received request to retrieve customer with ID: {}", custId);
                ResponseEntity<?> responseEntity;

                // Retrieve customer by ID
                Customer customer = customerService.getCustomerById(custId);
                log.info("Customer with ID {} retrieved successfully: {}", custId, customer);

                responseEntity = new ResponseEntity<>(
                                new ApiResponse("Success", HttpStatus.OK.value(),
                                                "Customer with ID " + custId + " retrieved successfully", customer),
                                HttpStatus.OK);

                return responseEntity;
        }

        /**
         * Generates a PDF file containing the list of all customers.
         *
         * @param response the HttpServletResponse object.
         * @return ResponseEntity containing the API response with the list of
         *         customers.
         * @throws IOException       if an input or output exception occurred.
         * @throws DocumentException if a document-related exception occurred.
         */
        @GetMapping("/customers/pdf")
        public ResponseEntity<byte[]> generateCustomerPdfFile(HttpServletResponse response,
                        @RequestHeader Map<String, String> headers) throws IOException, DocumentException {
                log.debug("Received a request to generate a PDF for customers");

                // Setting up response type
                response.setContentType("application/pdf");

                // Format the current date and time for the PDF filename
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String currentDateTime = dateFormat.format(new Date());

                // Set response headers for file download
                String headerKey = "Content-Disposition";
                String headerValue = "attachment; filename=Customers_" + currentDateTime + ".pdf";
                response.setHeader(headerKey, headerValue);

                // Retrieve all customers
                List<Customer> customers = customerService.getAllCustomers();
                log.info("PDF generated successfully!!");

                // Generate PDF of customer list
                ByteArrayOutputStream pdfBytesStream = new ByteArrayOutputStream();
                customerService.generateCustomerPdf(customers, pdfBytesStream);

                // Convert ByteArrayOutputStream to byte array
                byte[] pdfBytes = pdfBytesStream.toByteArray();

                // Return PDF bytes as ResponseEntity
                HttpHeaders headers1 = new HttpHeaders();
                headers1.setContentType(MediaType.APPLICATION_PDF);
                headers1.setContentDispositionFormData("filename", headerValue);
                headers1.setContentLength(pdfBytes.length);

                return new ResponseEntity<>(pdfBytes, headers1, HttpStatus.OK);
        }

        /**
         * Checks if the request body for customer-related endpoints is empty.
         *
         * @param request the customer request object.
         * @return true if the request body is empty, false otherwise.
         */
        private boolean isRequestBodyEmpty(Customer request) {
                return request.getEmailId() == null && request.getFullName() == null && request.getMobileNo() == null;

        }
}
