package com.creditmodule.loanmanagementapi.service.impl;

import com.creditmodule.loanmanagementapi.dto.response.InstallmentResponse;
import com.creditmodule.loanmanagementapi.dto.request.PayInstallmentRequest;
import com.creditmodule.loanmanagementapi.dto.response.PayInstallmentResult;
import com.creditmodule.loanmanagementapi.entity.Loan;
import com.creditmodule.loanmanagementapi.entity.LoanInstallment;
import com.creditmodule.loanmanagementapi.mapper.InstallmentMapper;
import com.creditmodule.loanmanagementapi.repository.LoanInstallmentRepository;
import com.creditmodule.loanmanagementapi.repository.LoanRepository;
import com.creditmodule.loanmanagementapi.service.IInstallmentService;

import com.creditmodule.loanmanagementapi.service.ILoanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class InstallmentServiceImpl implements IInstallmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(InstallmentServiceImpl.class);
    
    @Autowired
    private final LoanInstallmentRepository installmentRepository;

    @Autowired
    private final LoanRepository loanRepository;

    @Override
    public void generateInstallments(Loan loan) {
        logger.debug("Generating installments for loan ID: {} with amount: {} and {} installments", 
                    loan.getId(), loan.getLoanAmount(), loan.getNumberOfInstallments());
        
        BigDecimal annualRate = loan.getInterestRate();
        double monthlyInterestRate = annualRate.doubleValue() / 12 / 100;
        logger.debug("Annual interest rate: {}, monthly interest rate: {}", annualRate, monthlyInterestRate);

        int term = loan.getNumberOfInstallments();
        double principal = loan.getLoanAmount().doubleValue();
        logger.debug("Loan term: {} months, principal amount: {}", term, principal);

        double monthlyPayment = (principal * monthlyInterestRate) /
                (1 - Math.pow(1 + monthlyInterestRate, -term));
        logger.debug("Calculated monthly payment: {}", monthlyPayment);

        BigDecimal payment = BigDecimal.valueOf(monthlyPayment).setScale(2, RoundingMode.HALF_UP);
        logger.debug("Rounded monthly payment amount: {}", payment);

        for (int i = 1; i <= term; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setInstallmentNumber(i);
            installment.setAmount(payment);
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setDueDate(loan.getCreateDate().plusMonths(i));
            installment.setIsPaid(false);
            installmentRepository.save(installment);
            logger.debug("Created installment {}/{}: amount: {}, due date: {}", 
                        i, term, payment, loan.getCreateDate().plusMonths(i));
        }
        
        logger.debug("Successfully generated {} installments for loan ID: {}", term, loan.getId());
    }

    @Override
    public PayInstallmentResult payInstallment(PayInstallmentRequest request) {
        logger.debug("Processing installment payment for loan ID: {} with amount: {}", 
                    request.getLoanId(), request.getAmount());
        
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));
        logger.debug("Found loan ID: {}, isPaid: {}", loan.getId(), loan.getIsPaid());

        if (loan.getIsPaid()) {
            logger.debug("Loan {} is already fully paid", loan.getId());
            throw new IllegalStateException("Loan is already fully paid");
        }

        LoanInstallment installment = installmentRepository
                .findFirstByLoanIdAndIsPaidFalseOrderByDueDateAsc(request.getLoanId())
                .orElseThrow(() -> new IllegalStateException("No unpaid installments found"));
        logger.debug("Found next unpaid installment ID: {}, installment number: {}, amount: {}, due date: {}", 
                    installment.getId(), installment.getInstallmentNumber(), installment.getAmount(), installment.getDueDate());

        BigDecimal expectedAmount = installment.getAmount();
        BigDecimal paidAmount = request.getAmount();
        logger.debug("Expected amount: {}, paid amount: {}", expectedAmount, paidAmount);

        if (paidAmount.compareTo(expectedAmount) < 0) {
            logger.debug("Insufficient payment: expected {}, received {}", expectedAmount, paidAmount);
            throw new IllegalArgumentException("Installment must be paid in full");
        }

        installment.setPaidAmount(paidAmount);
        installment.setIsPaid(true);
        installment.setPaymentDate(LocalDate.now());
        installmentRepository.save(installment);
        logger.debug("Marked installment {} as paid with amount: {} on date: {}", 
                    installment.getId(), paidAmount, LocalDate.now());

        List<LoanInstallment> allInstallments = installmentRepository.findByLoanIdOrderByDueDate(request.getLoanId());
        boolean fullyPaid = allInstallments.stream().allMatch(LoanInstallment::getIsPaid);
        logger.debug("Checked all {} installments for loan {}, fully paid: {}", 
                    allInstallments.size(), loan.getId(), fullyPaid);

        if (fullyPaid) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
            logger.debug("Marked loan {} as fully paid", loan.getId());
        }

        PayInstallmentResult result = PayInstallmentResult.from(allInstallments, fullyPaid);
        logger.debug("Payment result created for loan {}: fully paid: {}", loan.getId(), fullyPaid);
        return result;
    }

    @Override
    public List<InstallmentResponse> getInstallmentsByLoan(Long loanId) {
        logger.debug("Retrieving all installments for loan ID: {}", loanId);
        
        List<LoanInstallment> installments = installmentRepository.findByLoanIdOrderByDueDate(loanId);
        logger.debug("Found {} installments for loan ID: {}", installments.size(), loanId);
        
        List<InstallmentResponse> responses = installments.stream()
                .map(InstallmentMapper::toResponse)
                .collect(Collectors.toList());
        
        logger.debug("Returning {} installment responses for loan ID: {}", responses.size(), loanId);
        return responses;
    }

    @Override
    public List<InstallmentResponse> getOverdueInstallments(Long loanId) {
        LocalDate today = LocalDate.now();
        logger.debug("Retrieving overdue installments for loan ID: {} as of date: {}", loanId, today);

        List<LoanInstallment> overdueInstallments = installmentRepository
                .findByLoanIdAndIsPaidFalseAndDueDateBefore(today);
        logger.debug("Found {} overdue installments for loan ID: {}", overdueInstallments.size(), loanId);
        
        List<InstallmentResponse> responses = overdueInstallments.stream()
                .map(InstallmentMapper::toResponse)
                .collect(Collectors.toList());
        
        logger.debug("Returning {} overdue installment responses for loan ID: {}", responses.size(), loanId);
        return responses;
    }
}