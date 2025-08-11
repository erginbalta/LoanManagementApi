package com.creditmodule.loanmanagementapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request payload for creating a loan")
public class CreateLoanRequest {

    @NotNull(message = "Customer ID is required")
    @Schema(description = "ID of the customer requesting the loan", example = "42")
    private Long customerId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Schema(description = "Principal loan amount", example = "10000.00", minimum = "0.01")
    private BigDecimal amount;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1")
    @DecimalMax(value = "0.5", message = "Interest rate must be at most 0.5")
    @Schema(description = "Interest rate as a decimal (e.g., 0.2 for 20%)", example = "0.2", minimum = "0.1", maximum = "0.5")
    private BigDecimal interestRate;

    @NotNull(message = "Number of installments is required")
    @Pattern(regexp = "^(6|9|12|24)$", message = "Number of installments must be 6, 9, 12, or 24")
    @Schema(description = "Number of installments (allowed values: 6, 9, 12, 24)", example = "12")
    private String numberOfInstallments;

}