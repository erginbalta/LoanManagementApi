package com.creditmodule.loanmanagementapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "loans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;
    
    @Column(nullable = false)
    private Integer numberOfInstallments;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;


    @Column(nullable = false)
    private LocalDate createDate;
    
    @Column(nullable = false)
    private Boolean isPaid = false;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LoanInstallment> installments;
}