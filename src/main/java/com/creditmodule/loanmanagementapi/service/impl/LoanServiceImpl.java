package com.creditmodule.loanmanagementapi.service.impl;

import com.creditmodule.loanmanagementapi.dto.request.PayLoanRequest;
import com.creditmodule.loanmanagementapi.dto.response.PaymentResult;
import com.creditmodule.loanmanagementapi.enums.InstallmentNumbers;
import com.creditmodule.loanmanagementapi.dto.request.CreateLoanRequest;
import com.creditmodule.loanmanagementapi.dto.response.LoanResponse;
import com.creditmodule.loanmanagementapi.entity.Customer;
import com.creditmodule.loanmanagementapi.entity.Loan;
import com.creditmodule.loanmanagementapi.entity.LoanInstallment;
import com.creditmodule.loanmanagementapi.exception.CreditLimitExceededException;
import com.creditmodule.loanmanagementapi.exception.CustomerNotFoundException;
import com.creditmodule.loanmanagementapi.exception.InvalidInstallmentException;
import com.creditmodule.loanmanagementapi.mapper.LoanMapper;
import com.creditmodule.loanmanagementapi.repository.CustomerRepository;
import com.creditmodule.loanmanagementapi.repository.LoanInstallmentRepository;
import com.creditmodule.loanmanagementapi.repository.LoanRepository;
import com.creditmodule.loanmanagementapi.service.ILoanService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements ILoanService {
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanInstallmentRepository installmentRepository;

    public LoanServiceImpl(LoanRepository loanRepository, CustomerRepository customerRepository,
                           LoanInstallmentRepository installmentRepository) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.installmentRepository = installmentRepository;
    }

    @Override
    @Transactional
    public LoanResponse createLoan(CreateLoanRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with ID: " + request.getCustomerId()));

        int numInstallments;
        try {
            numInstallments = Integer.parseInt(request.getNumberOfInstallments());
        } catch (NumberFormatException e) {
            throw new InvalidInstallmentException("Installment count must be a number.");
        }

        if (!isValidInstallment(numInstallments)) {
            throw new InvalidInstallmentException("Invalid number of installments. Valid options are: " +
                    Arrays.toString(InstallmentNumbers.values()));
        }

        BigDecimal totalLoanAmount = calculateTotalLoanAmount(request.getAmount(), request.getInterestRate());

        validateCreditLimit(customer, totalLoanAmount); // throws CreditLimitExceededException if needed

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(totalLoanAmount);
        loan.setNumberOfInstallments(numInstallments);
        loan.setInterestRate(request.getInterestRate());
        loan.setCreateDate(LocalDate.now());
        loan.setIsPaid(false);

        loan = loanRepository.save(loan);

        List<LoanInstallment> installments = generateInstallments(loan, numInstallments);
        loan.setInstallments(installments);
        installmentRepository.saveAll(installments);

        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(totalLoanAmount));
        customerRepository.save(customer);

        return LoanMapper.toResponse(loan);
    }

    @Override
    @Transactional
    public PaymentResult payLoan(PayLoanRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new CustomerNotFoundException("Loan not found with ID: " + request.getLoanId()));

        if (Boolean.TRUE.equals(loan.getIsPaid())) {
            throw new InvalidInstallmentException("Loan is already fully paid.");
        }

        List<LoanInstallment> unpaidInstallments = installmentRepository
                .findByLoanIdAndIsPaidFalseOrderByDueDateAsc(request.getLoanId());

        BigDecimal remainingAmount = request.getAmount();
        int paidCount = 0;
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (LoanInstallment installment : unpaidInstallments) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal installmentAmount = installment.getAmount();

            if (remainingAmount.compareTo(installmentAmount) >= 0) {
                installment.setIsPaid(true);
                installment.setPaymentDate(LocalDate.now());
                installmentRepository.save(installment);

                remainingAmount = remainingAmount.subtract(installmentAmount);
                totalSpent = totalSpent.add(installmentAmount);
                paidCount++;
            }
        }

        boolean loanFullyPaid = paidCount == loan.getNumberOfInstallments();

        if (loanFullyPaid) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
        }

        return PaymentResult.builder()
                .installmentsPaid(paidCount)
                .totalAmountSpent(totalSpent)
                .loanFullyPaid(loanFullyPaid)
                .build();
    }

    @Override
    @Transactional
    public LoanResponse getLoanDetails(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new CustomerNotFoundException("Loan not found with ID: " + loanId));

        return LoanMapper.toResponse(loan);
    }

    @Override
    @Transactional
    public List<LoanResponse> getLoansByCustomer(Long customerId) {
        List<Loan> loans = loanRepository.findByCustomerId(customerId);

        if (loans.isEmpty()) {
            throw new CustomerNotFoundException("No loans found for customer ID: " + customerId);
        }

        return loans.stream()
                .map(LoanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<LoanResponse> getLoansByCustomerWithFilters(Long customerId, Boolean isPaid, Integer numberOfInstallments) {
        List<Loan> loans = loanRepository.findByCustomerIdWithFilters(customerId, isPaid, numberOfInstallments);

        if (loans.isEmpty()) {
            throw new CustomerNotFoundException("No loans found for customer ID: " + customerId + " with given filters.");
        }

        return loans.stream()
                .map(LoanMapper::toResponse)
                .collect(Collectors.toList());
    }

    private PaymentResult buildPaymentResult(int paidCount, BigDecimal totalSpent, boolean loanFullyPaid) {
        return PaymentResult.builder()
                .installmentsPaid(paidCount)
                .totalAmountSpent(totalSpent)
                .loanFullyPaid(loanFullyPaid)
                .build();
    }

    private boolean isValidInstallment(int value) {
        for (InstallmentNumbers num : InstallmentNumbers.values()) {
            if (num.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    private BigDecimal calculateTotalLoanAmount(BigDecimal amount, BigDecimal interestRate) {
        return amount.multiply(interestRate.add(BigDecimal.ONE));
    }

    private void validateCreditLimit(Customer customer, BigDecimal requestedAmount) {
        BigDecimal availableLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());
        if (requestedAmount.compareTo(availableLimit) > 0) {
            throw new CreditLimitExceededException("Requested amount exceeds available credit limit.");
        }
    }

    private List<LoanInstallment> generateInstallments(Loan loan, int numInstallments) {
        BigDecimal installmentAmount = loan.getLoanAmount().divide(BigDecimal.valueOf(numInstallments), 2, RoundingMode.HALF_UP);
        List<LoanInstallment> installments = new ArrayList<>();
        LocalDate dueDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);

        for (int i = 0; i < numInstallments; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setDueDate(dueDate.plusMonths(i));
            installment.setIsPaid(false);
            installments.add(installment);
        }
        return installments;
    }
}
