package com.creditmodule.loanmanagementapi.controller;

import com.creditmodule.loanmanagementapi.dto.request.CreateCustomerRequest;
import com.creditmodule.loanmanagementapi.dto.response.CustomerResponse;
import com.creditmodule.loanmanagementapi.entity.Customer;
import com.creditmodule.loanmanagementapi.service.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;

    @Operation(
            summary = "Create a new customer",
            description = "Creates a customer with an initial credit limit. Used credit is set to zero by default.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Customer created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get customer by ID",
            description = "Retrieves customer details by their unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Customer found"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(description = "Customer ID", example = "1")
            @PathVariable Long id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Update customer's credit limit",
            description = "Updates the credit limit of a customer. New limit must be greater than zero.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Credit limit updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid credit limit"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @PutMapping("/{id}/credit-limit")
    public ResponseEntity<CustomerResponse> updateCreditLimit(
            @Parameter(description = "Customer ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "New credit limit", example = "15000.00")
            @RequestParam BigDecimal newLimit) {
        CustomerResponse response = customerService.updateCreditLimit(id, newLimit);
        return ResponseEntity.ok(response);
    }
}
