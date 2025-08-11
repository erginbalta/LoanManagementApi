package com.creditmodule.loanmanagementapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Response payload containing customer details")
public class CustomerResponse {

    @Schema(description = "Unique identifier of the customer", example = "1")
    private Long id;

    @Schema(description = "Customer's first name", example = "Ergin")
    private String name;

    @Schema(description = "Customer's last name", example = "Balta")
    private String surname;

    @Schema(description = "Total credit limit assigned to the customer", example = "10000.00")
    private BigDecimal creditLimit;

    @Schema(description = "Amount of credit already used by the customer", example = "2500.00")
    private BigDecimal usedCreditLimit;
}
