package com.springbootproject.springbootproject.Entitities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Bill {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @Temporal(TemporalType.DATE) // Field should only store the date part without any time information.
    private Date billDate;

    @ManyToOne
    @JoinColumn(name = "customerId") // Joining with Customer entity using customerId column
    private Customer customer; // Associated customer for this bill

    private Double billAmount; // Total amount of the bill

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<BillProduct> billProducts; // List of products in this bill

}
