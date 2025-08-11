package com.creditmodule.loanmanagementapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Request payload for creating a new customer")
public class CreateCustomerRequest {

    @NotBlank(message = "Customer name is required")
    @Schema(description = "Customer's first name", example = "Ergin")
    private String name;

    @NotBlank(message = "Customer surname is required")
    @Schema(description = "Customer's last name", example = "Balta")
    private String surname;

    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "0.01", message = "Credit limit must be greater than 0")
    @Schema(description = "Initial credit limit assigned to the customer", example = "10000.00", minimum = "0.01")
    private BigDecimal creditLimit;
}

