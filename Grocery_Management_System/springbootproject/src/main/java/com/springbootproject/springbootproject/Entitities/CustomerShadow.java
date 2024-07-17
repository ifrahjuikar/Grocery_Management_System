package com.springbootproject.springbootproject.Entitities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="customer_shadow")
// A shadow table same as that of customer table to store updated and deleted data.
public class CustomerShadow{
    
    @Id
    @Column(name="customer_id")
    private String customerId;

    @Column(nullable=false,name="full_name")
    private String fullName;

    @Column(name="email_id")
    private String emailId;

    @Column(nullable = false, name="mobile_no")
    private String mobileNo;

}
