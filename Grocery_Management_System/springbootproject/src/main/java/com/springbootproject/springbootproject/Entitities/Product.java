package com.springbootproject.springbootproject.Entitities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="product") // Specifies the name of the table in the database
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies the generation strategy for the primary key
    @Column(name="product_id") // Specifies the name of the column in the database
     // Primary key for the Product entity
    private Long productId;

    @Column(nullable=false,name="product_name") // Specifies the column properties
    // Name of the product
    private String productName; 

    @Column(name="quantity_available") // Specifies the name of the column in the database
    // Quantity available of the product
    private int quantityAvailable; 

    @Column(nullable = false, name="product_price") // Specifies the column properties
    // Price of the product
    private double productPrice; 
}
