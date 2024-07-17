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
@Table(name="customer") // Maps the entity to the "customer" table in the database
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Specifies auto-generation of the primary key
    @Column(name="customer_id") // Specifies the column name in the database
    // Unique identifier for the customer
    private Long customerId; 

    @Column(nullable=false, name="full_name") // Specifies that the column cannot be null
    // Full name of the customer
    private String fullName; 

    @Column(name="email_id") 
    // Email ID of the customer
    private String emailId;

    @Column(nullable = false, name="mobile_no")
    private String mobileNo; 
}
