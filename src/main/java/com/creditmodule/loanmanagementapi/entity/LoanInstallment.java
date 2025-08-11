package com.creditmodule.loanmanagementapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_installments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer installmentNumber;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = true)
    private LocalDate paymentDate;
    
    @Column(nullable = false)
    private Boolean isPaid = false;
}