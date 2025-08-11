package com.creditmodule.loanmanagementapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Response payload for a loan")
public class LoanResponse {

    @Schema(description = "Unique ID of the loan", example = "1001")
    private Long id;

    @Schema(description = "ID of the customer who owns the loan", example = "42")
    private Long customerId;

    @Schema(description = "Principal loan amount", example = "10000.00")
    private BigDecimal loanAmount;

    @Schema(description = "Total number of installments", example = "12")
    private Integer numberOfInstallment;

    @Schema(description = "Interest rate applied to the loan", example = "0.2")
    private BigDecimal interestRate;

    @Schema(description = "Date when the loan was created", example = "2025-08-11")
    private LocalDate createDate;

    @Schema(description = "Whether the loan has been fully paid", example = "false")
    private Boolean isPaid;
}