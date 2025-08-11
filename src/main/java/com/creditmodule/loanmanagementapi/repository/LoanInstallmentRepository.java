package com.creditmodule.loanmanagementapi.repository;

import com.creditmodule.loanmanagementapi.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    List<LoanInstallment> findByLoanIdOrderByDueDate(Long loanId);
    Optional<LoanInstallment> findFirstByLoanIdAndIsPaidFalseOrderByDueDateAsc(Long loanId);
    List<LoanInstallment> findByLoanIdAndIsPaidFalseAndDueDateBefore(LocalDate date);
    List<LoanInstallment> findByLoanIdAndIsPaidFalseOrderByDueDateAsc(Long loanId);
}
