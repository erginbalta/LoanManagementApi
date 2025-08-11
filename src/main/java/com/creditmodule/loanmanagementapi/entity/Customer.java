package com.creditmodule.loanmanagementapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String surname;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal creditLimit;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal usedCreditLimit = BigDecimal.ZERO;

}