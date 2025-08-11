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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstallmentServiceImpl implements IInstallmentService {

    private final LoanInstallmentRepository installmentRepository;
    private final LoanRepository loanRepository;

    @Override
    public void generateInstallments(Loan loan) {
        BigDecimal annualRate = loan.getInterestRate();
        double monthlyInterestRate = annualRate.doubleValue() / 12 / 100;

        int term = loan.getNumberOfInstallments();
        double principal = loan.getLoanAmount().doubleValue();

        double monthlyPayment = (principal * monthlyInterestRate) /
                (1 - Math.pow(1 + monthlyInterestRate, -term));

        BigDecimal payment = BigDecimal.valueOf(monthlyPayment).setScale(2, RoundingMode.HALF_UP);

        for (int i = 1; i <= term; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setInstallmentNumber(i);
            installment.setAmount(payment);
            installment.setPaidAmount(BigDecimal.ZERO);
            installment.setDueDate(loan.getCreateDate().plusMonths(i));
            installment.setIsPaid(false);
            installmentRepository.save(installment);
        }
    }

    @Override
    public PayInstallmentResult payInstallment(PayInstallmentRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.getIsPaid()) {
            throw new IllegalStateException("Loan is already fully paid");
        }

        LoanInstallment installment = installmentRepository
                .findFirstByLoanIdAndIsPaidFalseOrderByDueDateAsc(request.getLoanId())
                .orElseThrow(() -> new IllegalStateException("No unpaid installments found"));

        BigDecimal expectedAmount = installment.getAmount();
        BigDecimal paidAmount = request.getAmount();

        if (paidAmount.compareTo(expectedAmount) < 0) {
            throw new IllegalArgumentException("Installment must be paid in full");
        }

        installment.setPaidAmount(paidAmount);
        installment.setIsPaid(true);
        installment.setPaymentDate(LocalDate.now());
        installmentRepository.save(installment);

        List<LoanInstallment> allInstallments = installmentRepository.findByLoanIdOrderByDueDate(request.getLoanId());
        boolean fullyPaid = allInstallments.stream().allMatch(LoanInstallment::getIsPaid);

        if (fullyPaid) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
        }

        return PayInstallmentResult.from(allInstallments, fullyPaid);
    }

    @Override
    public List<InstallmentResponse> getInstallmentsByLoan(Long loanId) {
        return installmentRepository.findByLoanIdOrderByDueDate(loanId)
                .stream()
                .map(InstallmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<InstallmentResponse> getOverdueInstallments(Long loanId) {
        LocalDate today = LocalDate.now();

        return installmentRepository
                .findByLoanIdAndIsPaidFalseAndDueDateBefore(today)
                .stream()
                .map(InstallmentMapper::toResponse)
                .collect(Collectors.toList());
    }
}

