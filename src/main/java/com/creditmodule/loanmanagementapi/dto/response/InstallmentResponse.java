package com.creditmodule.loanmanagementapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "Response payload for a loan installment")
public class InstallmentResponse {

    @Schema(description = "Unique ID of the installment", example = "501")
    private Long id;

    @Schema(description = "ID of the associated loan", example = "1001")
    private Long loanId;

    @Schema(description = "Total amount due for this installment", example = "850.00")
    private BigDecimal amount;

    @Schema(description = "Amount already paid towards this installment", example = "850.00")
    private BigDecimal paidAmount;

    @Schema(description = "Due date of the installment", example = "2025-09-15")
    private LocalDate dueDate;

    @Schema(description = "Date when the installment was paid", example = "2025-09-10")
    private LocalDate paymentDate;

    @Schema(description = "Whether the installment has been fully paid", example = "true")
    private Boolean isPaid;
}
