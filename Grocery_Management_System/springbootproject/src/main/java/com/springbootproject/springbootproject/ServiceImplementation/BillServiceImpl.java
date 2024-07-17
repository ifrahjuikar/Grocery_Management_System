package com.springbootproject.springbootproject.ServiceImplementation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.springbootproject.springbootproject.Entitities.Bill;
import com.springbootproject.springbootproject.Entitities.BillProduct;
import com.springbootproject.springbootproject.Entitities.Customer;
import com.springbootproject.springbootproject.Entitities.Product;
import com.springbootproject.springbootproject.Exception.InsufficientProductQuantityException;
import com.springbootproject.springbootproject.Repositories.BillRepository;
import com.springbootproject.springbootproject.Repositories.ProductRepository;
import com.springbootproject.springbootproject.Service.BillService;
import com.springbootproject.springbootproject.Service.CustomerService;
import com.springbootproject.springbootproject.Service.ProductService;

/**
 * Implementation of the BillService interface.
 */
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    // Specify the directory where the PDF files will be saved
    private static final String PDF_DIRECTORY = "/home/ifrahj/Documents/SpringBoot-20240502T053928Z-001/SpringBoot/SpringBootAssignment/SpringBootMiniProject/springbootproject/bills";

    // Specify the file where the transactional list files will be saved
    private static final String EXCEL_PATH = "/home/ifrahj/Documents/SpringBoot-20240502T053928Z-001/SpringBoot/SpringBootAssignment/SpringBootMiniProject/springbootproject/excel/transactionallist.xlsx";

    /**
     * Generates a bill for the given bill request.
     * 
     * @param billRequest The bill request.
     * @return A map containing bill details.
     */
    @Override
    public Map<String, Object> generateBill(Bill billRequest) {
        Long customerId = billRequest.getCustomer().getCustomerId();
        List<BillProduct> products = billRequest.getBillProducts();

        Customer customer = customerService.getCustomerById(customerId);
        Bill bill = createBill(customer, products);
        double totalBillAmount = calculateTotalBillAmount(bill.getBillProducts());
        try {
            generateBillPDF(bill);
            appendToBillExcel(bill);
        } catch (IOException | DocumentException e) {
            e.getMessage(); // Handle the exception appropriately
        }

        return constructResponse(bill, customer, totalBillAmount);
    }

    /**
     * Creates a new bill based on the given customer and products.
     * 
     * @param customer The customer for whom the bill is generated.
     * @param products The list of products in the bill.
     * @return The created bill.
     */
    private Bill createBill(Customer customer, List<BillProduct> products) {
        Bill bill = new Bill();
        bill.setBillDate(new Date());
        bill.setCustomer(customer);

        List<BillProduct> billProducts = new ArrayList<>();
        for (BillProduct billProduct : products) {
            Long productId = billProduct.getProduct().getProductId();
            int quantity = billProduct.getQuantity();

            Product product = productService.getProductById(productId);
            if (product.getQuantityAvailable() < quantity) {
                throw new InsufficientProductQuantityException(
                        "Product out of stock: " + product.getProductName());
            }
            // Deduct quantity from product table
            product.setQuantityAvailable(product.getQuantityAvailable() - quantity);
            productRepository.save(product);

            // Create bill product
            BillProduct newBillProduct = new BillProduct();
            newBillProduct.setBill(bill);
            newBillProduct.setProduct(product);
            newBillProduct.setProductName(product.getProductName());
            newBillProduct.setQuantity(quantity);
            newBillProduct.setPrice(product.getProductPrice());
            double productBillPrice = quantity * product.getProductPrice();
            newBillProduct.setBillAmount(productBillPrice);

            billProducts.add(newBillProduct);
        }

        // Set bill products and total bill amount
        bill.setBillProducts(billProducts);
        bill.setBillAmount(calculateTotalBillAmount(billProducts));

        // Save bill
        return billRepository.save(bill);
    }

    /**
     * Calculates the total bill amount based on the list of bill products.
     * 
     * @param billProducts The list of bill products.
     * @return The total bill amount.
     */
    private double calculateTotalBillAmount(List<BillProduct> billProducts) {
        double totalBillAmount = 0;
        for (BillProduct billProduct : billProducts) {
            totalBillAmount += billProduct.getBillAmount();
        }
        return totalBillAmount;
    }

    /**
     * Constructs a response map containing bill details.
     * 
     * @param bill            The bill.
     * @param customer        The customer.
     * @param totalBillAmount The total bill amount.
     * @return A map containing bill details.
     */
    private Map<String, Object> constructResponse(Bill bill, Customer customer, double totalBillAmount) {
        Map<String, Object> response = new HashMap<>();
        response.put("billno", bill.getBillId());
        response.put("billdate", formatDate(bill.getBillDate()));
        response.put("customername", customer.getFullName());

        List<Map<String, Object>> productsList = new ArrayList<>();
        for (BillProduct billProduct : bill.getBillProducts()) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("productid", billProduct.getProduct().getProductId());
            productMap.put("name", billProduct.getProductName());
            productMap.put("qty", billProduct.getQuantity());
            productMap.put("price", billProduct.getPrice());
            productMap.put("billprice", billProduct.getBillAmount());
            productsList.add(productMap);
        }

        response.put("products", productsList);
        response.put("billamount", totalBillAmount);

        return response;
    }

    /**
     * Utility method to format Date object as String.
     * 
     * @param date The Date object.
     * @return A formatted date string.
     */
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    /**
     * Generates a PDF bill for the given bill object.
     * 
     * @param bill The bill object.
     * @throws FileNotFoundException If the PDF file is not found.
     * @throws DocumentException     If an error occurs during PDF document
     *                               handling.
     */
    private void generateBillPDF(Bill bill) throws FileNotFoundException, DocumentException {
        // Create the document
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(PDF_DIRECTORY + "/" + bill.getBillId() + ".pdf"));
        document.open();

        // Cash details at top middle with bold and bigger font size
        Font boldFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 20, Font.BOLD);
        Paragraph cashDetails = new Paragraph("Cash Details", boldFont);
        cashDetails.setAlignment(Element.ALIGN_CENTER);
        document.add(cashDetails);
        document.add(new Paragraph("\n"));

        // Bill details on the left side
        document.add(new Paragraph("Bill ID: " + bill.getBillId().toString()));
        document.add(new Paragraph("Bill Date: " + formatDate(bill.getBillDate())));
        document.add(new Paragraph("Customer Name: " + bill.getCustomer().getFullName()));
        document.add(new Paragraph("\n"));

        // Product details in a table
        PdfPTable productTable = new PdfPTable(4);
        productTable.setWidthPercentage(100);
        productTable.addCell(createCell("Product Name"));
        productTable.addCell(createCell("MRP"));
        productTable.addCell(createCell("Quantity"));
        productTable.addCell(createCell("Total Price"));

        List<BillProduct> billProducts = bill.getBillProducts();
        for (BillProduct billProduct : billProducts) {
            productTable.addCell(createCell(billProduct.getProductName()));
            productTable.addCell(createCell(String.valueOf(billProduct.getPrice())));
            productTable.addCell(createCell(String.valueOf(billProduct.getQuantity())));
            productTable.addCell(createCell(String.valueOf(billProduct.getBillAmount())));
        }
        document.add(productTable);
        document.add(new Paragraph("\n"));

        // Total Bill Amount aligned to the right
        Paragraph totalAmount = new Paragraph("Total Bill Amount: " + bill.getBillAmount());
        totalAmount.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalAmount);

        // Thank you message in the middle
        Paragraph thankYou = new Paragraph("Thank You", boldFont);
        thankYou.setAlignment(Element.ALIGN_CENTER);
        document.add(thankYou);

        // Close the document
        document.close();
    }

    private PdfPCell createCell(String label) {
        PdfPCell cell = new PdfPCell(new Paragraph(label));
        cell.setPadding(5);
        return cell;
    }

    /**
     * Appends bill details to an Excel file.
     * 
     * @param bill The bill object.
     * @throws IOException If an I/O error occurs while reading or writing.
     */
    private void appendToBillExcel(Bill bill) throws IOException {
        Workbook workbook;
        Sheet sheet;
        File excelFile = new File(EXCEL_PATH);

        if (excelFile.exists()) {
            FileInputStream fis = new FileInputStream(excelFile);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0);
            fis.close();
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Bills");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Bill ID");
            header.createCell(1).setCellValue("Bill Date");
            header.createCell(2).setCellValue("Customer ID");
            header.createCell(3).setCellValue("Customer Name");
            header.createCell(4).setCellValue("Product ID");
            header.createCell(5).setCellValue("Product Name");
            header.createCell(6).setCellValue("Quantity");
            header.createCell(7).setCellValue("Unit Price");
            header.createCell(8).setCellValue("Product Total");
            header.createCell(9).setCellValue("Bill Amount");
        }

        // sheet.getRow(0).createCell(9).setCellValue("Customer ID");

        int lastRow = sheet.getLastRowNum();

        for (BillProduct billProduct : bill.getBillProducts()) {
            Row row = sheet.createRow(++lastRow);

            row.createCell(0).setCellValue(bill.getBillId());
            row.createCell(1).setCellValue(formatDate(bill.getBillDate()));
            row.createCell(2).setCellValue(bill.getCustomer().getCustomerId());
            row.createCell(3).setCellValue(bill.getCustomer().getFullName());
            row.createCell(9).setCellValue(bill.getBillAmount());

            row.createCell(4).setCellValue(billProduct.getProduct().getProductId());
            row.createCell(5).setCellValue(billProduct.getProduct().getProductName());

            row.createCell(6).setCellValue(billProduct.getQuantity());
            row.createCell(7).setCellValue(billProduct.getPrice());
            row.createCell(8).setCellValue(billProduct.getQuantity() * billProduct.getPrice());
        }

        FileOutputStream fileOut = new FileOutputStream(EXCEL_PATH);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }
}

// @Override
// public Map<String, Object> generateBill(Bill billRequest) {
// Long customerId = billRequest.getCustomer().getCustomerId();
// List<BillProduct> products = billRequest.getBillProducts();

// // Optional<Customer> customerOptional =
// customerRepository.findById(customerId);
// // if (!customerOptional.isPresent()) {
// // throw new CustomerNotFoundException("Customer not found for customerId: "
// + customerId);
// // }
// // Customer customer = customerOptional.get();
// //or
// Customer customer=customerService.getCustomerById(customerId);

// // Create new bill
// Bill bill = new Bill();
// bill.setBillDate(new Date());
// bill.setCustomer(customer);

// List<BillProduct> billProducts = new ArrayList<>();
// double totalBillAmount = 0;

// // Process each product
// for (BillProduct bills : products) {
// Long productId = bills.getProduct().getProductId();
// int quantity = bills.getQuantity();

// // // Fetch product
// // Optional<Product> productOptional = productRepository.findById(productId);
// // if (!productOptional.isPresent()) {
// // throw new ProductNotFoundException("Product not found for productId: " +
// productId);
// // }

// // Product product = productOptional.get();
// //or
// Product product = productService.getProductById(productId);

// // Check product quantity
// if (product.getQuantityAvailable() < quantity) {
// throw new InsufficientProductQuantityException("Insufficient quantity for
// product: " + product.getProductName());
// }

// // Check product quantity
// if (product.getQuantityAvailable() < quantity) {

// }

// // Deduct quantity from product table
// product.setQuantityAvailable(product.getQuantityAvailable() - quantity);

// // Create bill product
// BillProduct billProduct = new BillProduct();
// billProduct.setBill(bill);
// billProduct.setProduct(product);
// billProduct.setProductName(product.getProductName());
// billProduct.setQuantity(quantity);
// billProduct.setPrice(product.getProductPrice());
// double productBillPrice = quantity * product.getProductPrice();
// billProduct.setBillAmount(productBillPrice);

// // Add bill product to list
// billProducts.add(billProduct);

// // Update total bill amount
// totalBillAmount += productBillPrice;

// // Save updated product quantity
// productRepository.save(product);
// }

// // Set bill products and total bill amount
// bill.setBillProducts(billProducts);
// bill.setBillAmount(totalBillAmount);

// // Save bill and associated products
// bill = billRepository.save(bill);

// // Construct the response
// Map<String, Object> response = new HashMap<>();
// response.put("billno", bill.getBillId());
// response.put("billdate", formatDate(bill.getBillDate()));
// response.put("customername", customer.getFullName());

// List<Map<String, Object>> productsList = new ArrayList<>();
// for (BillProduct billProduct : bill.getBillProducts()) {
// Map<String, Object> productMap = new HashMap<>();
// productMap.put("productid", billProduct.getProduct().getProductId());
// productMap.put("name", billProduct.getProductName());
// productMap.put("qty", billProduct.getQuantity());
// productMap.put("price", billProduct.getPrice());
// productMap.put("billprice", billProduct.getBillAmount());
// productsList.add(productMap);
// }

// response.put("products", productsList);
// response.put("billamount", totalBillAmount);

// return response;
// }
