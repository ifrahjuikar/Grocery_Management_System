package com.springbootproject.springbootproject.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;
import com.springbootproject.springbootproject.Entitities.Product;
import com.springbootproject.springbootproject.Exception.ApiResponse;
import com.springbootproject.springbootproject.Service.ProductService;


/**
 * Controller class for handling CRUD operations on Products.
 */
@RestController
public class ProductController {
        
        private static final Logger log = LoggerFactory.getLogger(ProductController.class);

        @Autowired
        private ProductService productService;

        /**
         * Adds a new product.
         *
         * @param product the product to be added.
         * @return ResponseEntity containing the API response.
         */
        @PostMapping("/product")
        public ResponseEntity<?> addProduct(@RequestBody Product product) {
                ResponseEntity<?> responseEntity;
                log.debug("Request to add a product");
                // Check if the request body is empty
                if (isRequestBodyEmpty(product)) {
                        log.warn("Request body is empty: {}", product);
                        responseEntity = new ResponseEntity<>(new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(),
                                        "Request Body is Empty", null), HttpStatus.BAD_REQUEST);
                } else {
                        // Add the product and log the success
                        Product addedProduct = productService.addProduct(product);
                        log.info("Product added successfully: {}", product);
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(), "Product added successfully",
                                                        addedProduct),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Updates an existing product.
         *
         * @param product the product with updated details.
         * @return ResponseEntity containing the API response.
         */
        @PutMapping("/product")
        public ResponseEntity<?> updateProduct(@RequestBody Product product) {
                ResponseEntity<?> responseEntity;
                log.debug("Received a request to modify a product");

                // Check if the request body is empty
                if (isRequestBodyEmpty(product)) {
                        log.warn(" Received request body is empty: {}", product);
                        responseEntity = new ResponseEntity<>(new ApiResponse("Error", HttpStatus.BAD_REQUEST.value(),
                                        "Request Body is Empty", null), HttpStatus.BAD_REQUEST);
                } else {
                        // Update the product and log the success
                        Product updatedProduct = productService.updateProduct(product);
                        Long productId = product.getProductId();

                        log.info("Product updated successfully with ID: {}", productId);
                        responseEntity = new ResponseEntity<>(
                                        new ApiResponse("Success", HttpStatus.OK.value(),
                                                        "Product with product id " + productId
                                                                        + " updated successfully",
                                                        updatedProduct),
                                        HttpStatus.OK);
                }
                return responseEntity;
        }

        /**
         * Deletes a product by its ID.
         *
         * @param productId the ID of the product to be deleted.
         * @return ResponseEntity containing the API response.
         */
        @DeleteMapping("/product/{productId}")
        public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
                ResponseEntity<?> responseEntity;
                log.debug("Received a request to delete a product");

                // Delete the product and log the success
                productService.deleteProduct(productId);
                log.info("Product deleted successfully with ID: {}", productId);

                responseEntity = new ResponseEntity<>(
                                new ApiResponse("Success", HttpStatus.NO_CONTENT.value(),
                                                "Product deleted successfully", null),
                                HttpStatus.NO_CONTENT);
                return responseEntity;
        }

        /**
         * Retrieves all products.
         *
         * @return ResponseEntity containing the list of all products.
         */
        @GetMapping("/products")
        public ResponseEntity<?> getAllProducts() {
                ResponseEntity<?> responseEntity;
                log.debug("Received a request to retrive a products");

                // Retrieve all products and log the success
                List<Product> products = productService.getAllProducts();
                log.info("Products retrieved successfully!!");

                responseEntity = new ResponseEntity<>(
                                new ApiResponse("Success", HttpStatus.OK.value(), "Retrieved all products", products),
                                HttpStatus.OK);
                return responseEntity;
        }

        /**
         * Retrieves a product by its ID.
         *
         * @param productId the ID of the product to be retrieved.
         * @return ResponseEntity containing the product details.
         */
        @GetMapping("/product/{productId}")
        public ResponseEntity<?> getProductById(@PathVariable Long productId) {
                ResponseEntity<?> responseEntity;
                log.debug("Received a request to retrive a product with Id " + productId);

                // Retrieve the product by ID and log the success
                Product product = productService.getProductById(productId);
                log.info("Product with ID: {} retrieved successfully", productId);

                responseEntity = new ResponseEntity<>(
                                new ApiResponse("Success", HttpStatus.OK.value(),
                                                "Product with product id " + productId + " retrieved successfully",
                                                product),
                                HttpStatus.OK);
                return responseEntity;
        }

        /**
         * Generates a PDF file containing the list of all products.
         *
         * @param response the HTTP response.
         * @param headers  the HTTP headers.
         * @return ResponseEntity containing the API response.
         * @throws IOException       if an I/O error occurs.
         * @throws DocumentException if a document generation error occurs.
         */
        @GetMapping("/products/pdf")
        public ResponseEntity<byte[]> generateProductPdfFile(HttpServletResponse response,
                        @RequestHeader Map<String, String> headers) throws IOException, DocumentException {
                log.debug("Received a request to generate a pdf for products");

                // Setting up response type
                response.setContentType("application/pdf");

                // Format the current date and time for the PDF filename
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                String currentDateTime = dateFormat.format(new Date());

                // Set response headers for file download
                String headerKey = "Content-Disposition";
                String headerValue = "attachment; filename=Product_" + currentDateTime + ".pdf";
                response.setHeader(headerKey, headerValue);

                // Retrieve all products
                List<Product> products = productService.getAllProducts();
                log.info("Pdf generated Successfully!!");
                // Generate PDF of product list
                ByteArrayOutputStream pdfBytesStream = new ByteArrayOutputStream();
                productService.generateProductPdf(products, pdfBytesStream);

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
         * Checks if the request body is empty.
         *
         * @param request the product request.
         * @return true if the request body is empty, false otherwise.
         */
        private boolean isRequestBodyEmpty(Product request) {
                return request.getProductName() == null && request.getProductPrice() == 0;
        }
}
