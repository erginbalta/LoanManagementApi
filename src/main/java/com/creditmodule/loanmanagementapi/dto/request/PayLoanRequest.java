package com.creditmodule.loanmanagementapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request payload for paying off a loan")
public class PayLoanRequest {

    @NotNull(message = "Loan ID is required")
    @Schema(description = "ID of the loan being paid", example = "1001")
    private Long loanId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Amount to be paid towards the loan", example = "5000.00", minimum = "0.01")
    private BigDecimal amount;
}