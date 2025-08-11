package com.creditmodule.loanmanagementapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Result of a loan payment operation")
public class PaymentResult {

    @Schema(description = "Number of installments paid during this operation", example = "5")
    private Integer installmentsPaid;

    @Schema(description = "Total amount spent in this payment", example = "4250.00")
    private BigDecimal totalAmountSpent;

    @Schema(description = "Whether the loan has been fully paid after this payment", example = "true")
    private Boolean loanFullyPaid;
}