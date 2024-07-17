package com.springbootproject.springbootproject.ServiceImplementation;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.springbootproject.springbootproject.Entitities.Customer;
import com.springbootproject.springbootproject.Entitities.CustomerShadow;
import com.springbootproject.springbootproject.Exception.CustomerNotFoundException;
import com.springbootproject.springbootproject.Repositories.CustomerRepository;
import com.springbootproject.springbootproject.Repositories.CustomerShadowRepository;
import com.springbootproject.springbootproject.Service.CustomerService;

/**
 * Service implementation class for handling customer-related operations.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerShadowRepository customerShadowRepository;

    /**
     * Adds a new customer.
     * 
     * @param customer The customer to be added.
     * @return The added customer.
     */
    @Override
    public Customer addCustomer(Customer customer) {
        // Adding a new customer
        Customer addedCustomer = customerRepository.save(customer);
        return addedCustomer;
    }

    /**
     * Updates an existing customer.
     * 
     * @param customer The customer with updated details.
     * @return The updated customer.
     */
    @Override
    public Customer updateCustomer(Customer customer) {
        // Updating an existing customer
        Long customerId = customer.getCustomerId(); // Assuming there's a getCustomerId() method in Customer class
        Customer existingCustomer = customerRepository.findById(customerId) // find customer by id
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found with customer id: " + customerId));

        copyCustomerToShadow(existingCustomer); // Copying customer details to shadow table

        // Updating customer details
        if (customer.getFullName() != null) {
            existingCustomer.setFullName(customer.getFullName());
        }
        if (customer.getEmailId() != null) {
            existingCustomer.setEmailId(customer.getEmailId());
        }
        if (customer.getMobileNo() != null) {
            existingCustomer.setMobileNo(customer.getMobileNo());
        }

        // Saving the updated customer details
        return customerRepository.save(existingCustomer);
    }

    /**
     * Deletes a customer.
     * 
     * @param custId The ID of the customer to be deleted.
     * @return The deleted customer.
     */
    @Override
    public Customer deleteCustomer(Long custId) {
        // Deleting a customer
        Customer existingCustomer = customerRepository.findById(custId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found with customer id: " + custId));

        copyCustomerToShadow(existingCustomer); // Copying customer details to shadow table

        // Deleting the customer
        customerRepository.delete(existingCustomer);

        return existingCustomer;
    }

    /**
     * Retrieves all customers.
     * 
     * @return A list of all customers.
     */
    @Override
    public List<Customer> getAllCustomers() {
        // Retrieving all customers
        List<Customer> customers = customerRepository.findAll();
        return customers;
    }

    /**
     * Retrieves a customer by ID.
     * 
     * @param custId The ID of the customer to be retrieved.
     * @return The customer with the specified ID.
     */
    @Override
    public Customer getCustomerById(Long custId) {
        // Retrieving a customer by ID
        Customer customer = customerRepository.findById(custId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found with customer id: " + custId));
        return customer;
    }

    /**
     * Copies the information from a Customer object to a CustomerShadow object and
     * saves it.
     * This method is used to create a shadow copy of a Customer entity for auditing
     * or other purposes.
     *
     * @param customer The Customer object from which to copy the information.
     */
    private void copyCustomerToShadow(Customer customer) {
        // Create a new CustomerShadow object
        CustomerShadow customerShadow = new CustomerShadow();
        // set customer details
        customerShadow.setCustomerId(String.valueOf(customer.getCustomerId()));
        customerShadow.setFullName(customer.getFullName());
        customerShadow.setEmailId(customer.getEmailId());
        customerShadow.setMobileNo(customer.getMobileNo());

        // Save the CustomerShadow object to the repository
        customerShadowRepository.save(customerShadow);
    }

    /**
     * Generates a PDF containing details of multiple customers.
     * 
     * @param customerList A list of customers to include in the PDF.
     * @param response     The HttpServletResponse object to write the PDF to.
     * @throws DocumentException Thrown if there is an error while generating the
     *                           PDF document.
     * @throws IOException       Thrown if there is an error while writing the PDF
     *                           to the HttpServletResponse.
     */
    @Override
    public void generateCustomerPdf(List<Customer> customerList, OutputStream outputStream)
            throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);
        document.open();
        addCustomerDetailsToPdf(document, customerList);
        document.close();
    }

    /**
     * Adds customer details to the PDF document.
     *
     * @param document     The PDF document to which customer details will be added.
     * @param customerList The list of customers whose details will be added to the
     *                     PDF.
     * @throws DocumentException If an error occurs while adding customer details to
     *                           the document.
     */
    private void addCustomerDetailsToPdf(Document document, List<Customer> customerList) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 2, 3, 4, 3 });
        table.setSpacingBefore(10);
        addTableHeader(table);
        // Add customer details to the table
        for (Customer customer : customerList) {
            table.addCell(String.valueOf(customer.getCustomerId()));
            table.addCell(customer.getFullName());
            table.addCell(customer.getEmailId());
            table.addCell(customer.getMobileNo());
        }
        document.add(table);// Add the table to the document
    }

    /**
     * Adds the header row to the PDF table.
     *
     * @param table The PDF table to which the header row will be added.
     */
    private void addTableHeader(PdfPTable table) {
        // Define font for the header
        Font fontHeader = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);
        // Create a cell for the header
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(0, 100, 255));
        // Set padding for the cell
        cell.setPadding(5);
        // Array of header names
        String[] headers = { "Customer ID", "Full Name", "Email ID", "Mobile No" };
        // Add headers to the table
        for (String header : headers) {
            cell.setPhrase(new Phrase(header, fontHeader));
            table.addCell(cell);
        }
    }
}
