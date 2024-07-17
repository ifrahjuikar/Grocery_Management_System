package com.springbootproject.springbootproject.Controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.springbootproject.springbootproject.Entitities.Bill;
import com.springbootproject.springbootproject.Exception.ApiResponse;
import com.springbootproject.springbootproject.Service.BillService;

/**
 * Controller class for managing bill-related endpoints.
 */
@RestController
public class BillController {

    private static final Logger log = LoggerFactory.getLogger(BillController.class);

    @Autowired
    private BillService billService;

    /**
     * Endpoint to generate a bill.
     *
     * @param billRequest the bill request object containing bill details.
     * @return ResponseEntity containing the API response with bill details.
     */
    @PostMapping("/bill")
    public ResponseEntity<?> generateBills(@RequestBody Bill billRequest) {
        ResponseEntity<?> responseEntity;
        log.debug("Received request to generate bill for customerid: " + billRequest.getCustomer().getCustomerId());

        // Call the service to generate the bill
        Map<String, Object> billResponse = billService.generateBill(billRequest);
        log.info("Generated bill successfully: " + billResponse);

        // Create successful API response
        responseEntity = new ResponseEntity<>(
                new ApiResponse("Success", HttpStatus.OK.value(), "Generated Bill Successfully", billResponse),
                HttpStatus.OK);

        // Return response entity
        return responseEntity;
    }
}
