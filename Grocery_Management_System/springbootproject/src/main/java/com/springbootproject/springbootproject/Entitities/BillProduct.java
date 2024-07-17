package com.springbootproject.springbootproject.Entitities;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "billProducts") // Specifies the name of the table in the database
public class BillProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies the generation strategy for the primary key
    private Long id; // Primary key for the BillProduct entity

    @ManyToOne
    @JoinColumn(name = "billId") // Joining with Bill entity using billId column
    private Bill bill; // Associated bill for this BillProduct

    @ManyToOne
    @JoinColumn(name = "productId") // Joining with Product entity using productId column
    private Product product; // Associated product for this BillProduct

    private String productName; // Name of the product
    private int quantity; // Quantity of the product
    private double price; // Price per unit of the product
    private double billAmount; // Total amount for this line item in the bill
}
