package com.creditmodule.loanmanagementapi.dto.response;

import com.creditmodule.loanmanagementapi.entity.LoanInstallment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
@Schema(description = "Result of paying one or more installments")
public class PayInstallmentResult {

    @Schema(description = "Number of installments that have been paid", example = "3")
    private int numberOfInstallmentsPaid;

    @Schema(description = "Total amount paid across installments", example = "2550.00")
    private BigDecimal totalAmountSpent;

    @Schema(description = "Whether the loan is now fully paid", example = "false")
    private boolean isLoanFullyPaid;


    public PayInstallmentResult(int numberOfInstallmentsPaid, BigDecimal totalAmountSpent, boolean isLoanFullyPaid) {
        this.numberOfInstallmentsPaid = numberOfInstallmentsPaid;
        this.totalAmountSpent = totalAmountSpent;
        this.isLoanFullyPaid = isLoanFullyPaid;
    }

    public static PayInstallmentResult from(List<LoanInstallment> installments, boolean isLoanFullyPaid) {
        int paidCount = (int) installments.stream().filter(LoanInstallment::getIsPaid).count();
        BigDecimal totalPaid = installments.stream()
                .map(LoanInstallment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new PayInstallmentResult(paidCount, totalPaid, isLoanFullyPaid);
    }
}
