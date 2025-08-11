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

    @Query("SELECT li FROM LoanInstallment li WHERE li.loan.id = :loanId ORDER BY li.dueDate ASC")
    List<LoanInstallment> findByLoanIdOrderByDueDate(@Param("loanId") Long loanId);

    @Query("SELECT li FROM LoanInstallment li WHERE li.loan.id = :loanId AND li.isPaid = false ORDER BY li.dueDate ASC")
    Optional<LoanInstallment> findFirstByLoanIdAndIsPaidFalseOrderByDueDateAsc(@Param("loanId") Long loanId);

    @Query("SELECT li FROM LoanInstallment li WHERE li.loan.id = :loanId AND li.isPaid = false AND li.dueDate < :date")
    List<LoanInstallment> findByLoanIdAndIsPaidFalseAndDueDateBefore(@Param("date") LocalDate date);

    @Query("SELECT li FROM LoanInstallment li WHERE li.loan.id = :loanId AND li.isPaid = false ORDER BY li.dueDate ASC")
    List<LoanInstallment> findByLoanIdAndIsPaidFalseOrderByDueDateAsc(@Param("loanId") Long loanId);
}

