package com.creditmodule.loanmanagementapi.repository;

import com.creditmodule.loanmanagementapi.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerId(Long customerId);
    
    @Query("SELECT l FROM Loan l WHERE l.customer.id = :customerId AND (:isPaid IS NULL OR l.isPaid = :isPaid) AND (:numberOfInstallments IS NULL OR l.numberOfInstallments = :numberOfInstallments)")
    List<Loan> findByCustomerIdWithFilters(@Param("customerId") Long customerId, 
                                         @Param("isPaid") Boolean isPaid, 
                                         @Param("numberOfInstallments") Integer numberOfInstallments);
}