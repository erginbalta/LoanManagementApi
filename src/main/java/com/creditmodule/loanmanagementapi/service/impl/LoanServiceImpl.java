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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);

    @Autowired
    private final LoanRepository loanRepository;

    @Autowired
    private final CustomerRepository customerRepository;

    @Autowired
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
        logger.debug("Creating loan with request: {}", request);
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

        validateCreditLimit(customer, totalLoanAmount);

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(totalLoanAmount);
        loan.setNumberOfInstallments(numInstallments);
        loan.setInterestRate(request.getInterestRate());
        loan.setCreateDate(LocalDate.now());
        loan.setIsPaid(false);
        logger.debug("Saving loan with request: {}", loan);

        loan = loanRepository.save(loan);

        List<LoanInstallment> installments = generateInstallments(loan, numInstallments);
        loan.setInstallments(installments);
        installmentRepository.saveAll(installments);

        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(totalLoanAmount));
        customerRepository.save(customer);

        logger.debug("Created loan: {}", loan);
        return LoanMapper.toResponse(loan);
    }

    @Override
    @Transactional
    public PaymentResult payLoan(PayLoanRequest request) {
        logger.debug("Paying loan with request: {}", request);
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

        PaymentResult result = buildPaymentResult(paidCount, totalSpent, loanFullyPaid);
        logger.debug("Payment result: {}", result);
        return result;
    }

    @Override
    @Transactional
    public LoanResponse getLoanDetails(Long loanId) {
        logger.debug("Getting loan details for loan ID: {}", loanId);
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new CustomerNotFoundException("Loan not found with ID: " + loanId));

        LoanResponse response = LoanMapper.toResponse(loan);
        logger.debug("Loan details: {}", response);
        return response;
    }

    @Override
    @Transactional
    public List<LoanResponse> getLoansByCustomer(Long customerId) {
        logger.debug("Getting loans for customer ID: {}", customerId);
        List<Loan> loans = loanRepository.findByCustomerId(customerId);

        if (loans.isEmpty()) {
            throw new CustomerNotFoundException("No loans found for customer ID: " + customerId);
        }

        List<LoanResponse> responses = loans.stream()
                .map(LoanMapper::toResponse)
                .collect(Collectors.toList());
        logger.debug("Loans for customer: {}", responses);
        return responses;
    }

    @Override
    @Transactional
    public List<LoanResponse> getLoansByCustomerWithFilters(Long customerId, Boolean isPaid, Integer numberOfInstallments) {
        logger.debug("Getting loans for customer ID: {} with filters isPaid: {}, numberOfInstallments: {}", customerId, isPaid, numberOfInstallments);
        List<Loan> loans = loanRepository.findByCustomerIdWithFilters(customerId, isPaid, numberOfInstallments);

        if (loans.isEmpty()) {
            throw new CustomerNotFoundException("No loans found for customer ID: " + customerId + " with given filters.");
        }

        List<LoanResponse> responses = loans.stream()
                .map(LoanMapper::toResponse)
                .collect(Collectors.toList());
        logger.debug("Filtered loans for customer: {}", responses);
        return responses;
    }

    private PaymentResult buildPaymentResult(int paidCount, BigDecimal totalSpent, boolean loanFullyPaid) {
        PaymentResult result = PaymentResult.builder()
                .installmentsPaid(paidCount)
                .totalAmountSpent(totalSpent)
                .loanFullyPaid(loanFullyPaid)
                .build();
        logger.debug("Built payment result: {}", result);
        return result;
    }

    private boolean isValidInstallment(int value) {
        logger.debug("Checking if installment value {} is valid", value);
        for (InstallmentNumbers num : InstallmentNumbers.values()) {
            if (num.getValue() == value) {
                logger.debug("Installment value {} is valid", value);
                return true;
            }
        }
        logger.debug("Installment value {} is invalid", value);
        return false;
    }

    private BigDecimal calculateTotalLoanAmount(BigDecimal amount, BigDecimal interestRate) {
        BigDecimal total = amount.multiply(interestRate.add(BigDecimal.ONE));
        logger.debug("Calculated total loan amount: {}", total);
        return total;
    }

    private void validateCreditLimit(Customer customer, BigDecimal requestedAmount) {
        BigDecimal availableLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());
        if (requestedAmount.compareTo(availableLimit) > 0) {
            throw new CreditLimitExceededException("Requested amount exceeds available credit limit.");
        }
        logger.debug("Credit limit validated for customer: {}", customer.getId());
    }

    private List<LoanInstallment> generateInstallments(Loan loan, int numInstallments) {
        List<LoanInstallment> installments = new ArrayList<>();
        BigDecimal installmentAmount = loan.getLoanAmount()
                .divide(BigDecimal.valueOf(numInstallments), 2, RoundingMode.HALF_UP);

        for (int i = 1; i <= numInstallments; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setInstallmentNumber(i);
            installment.setAmount(installmentAmount);
            installment.setDueDate(LocalDate.now().plusMonths(i));
            installment.setIsPaid(false);
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setPaymentDate(null);

            installments.add(installment);
        }

        return installments;
    }
}