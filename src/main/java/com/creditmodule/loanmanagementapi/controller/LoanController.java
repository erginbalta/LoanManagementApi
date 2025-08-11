package com.creditmodule.loanmanagementapi.controller;

import com.creditmodule.loanmanagementapi.dto.request.CreateLoanRequest;
import com.creditmodule.loanmanagementapi.dto.request.PayLoanRequest;
import com.creditmodule.loanmanagementapi.dto.response.LoanResponse;
import com.creditmodule.loanmanagementapi.dto.response.PaymentResult;
import com.creditmodule.loanmanagementapi.entity.Loan;
import com.creditmodule.loanmanagementapi.service.ILoanService;
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

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final ILoanService loanService;

    @Operation(
            summary = "Create a new loan",
            description = "Creates a loan for a customer with specified amount and installment details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Loan created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid loan request")
            }
    )
    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(
            @Valid @RequestBody CreateLoanRequest request) {
        LoanResponse response = loanService.createLoan(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Pay an installment for a loan",
            description = "Processes a payment for a specific loan installment.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Payment processed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid payment request"),
                    @ApiResponse(responseCode = "404", description = "Loan or installment not found")
            }
    )
    @PostMapping("/pay")
    public ResponseEntity<PaymentResult> payLoan(
            @Valid @RequestBody PayLoanRequest request) {
        PaymentResult result = loanService.payLoan(request);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get loan details by ID",
            description = "Retrieves detailed information about a specific loan.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Loan details retrieved"),
                    @ApiResponse(responseCode = "404", description = "Loan not found")
            }
    )
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanResponse> getLoanDetails(
            @Parameter(description = "Loan ID", example = "1001")
            @PathVariable Long loanId) {
        LoanResponse response = loanService.getLoanDetails(loanId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get all loans for a customer",
            description = "Returns a list of all loans associated with a specific customer.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Loans retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<LoanResponse>> getLoansByCustomer(
            @Parameter(description = "Customer ID", example = "42")
            @PathVariable Long customerId) {
        List<LoanResponse> loans = loanService.getLoansByCustomer(customerId);
        return ResponseEntity.ok(loans);
    }

    @Operation(
            summary = "Get filtered loans for a customer",
            description = "Returns loans for a customer filtered by payment status and number of installments.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered loans retrieved"),
                    @ApiResponse(responseCode = "404", description = "Customer not found")
            }
    )
    @GetMapping("/customer/{customerId}/filter")
    public ResponseEntity<List<LoanResponse>> getLoansByCustomerWithFilters(
            @Parameter(description = "Customer ID", example = "42")
            @PathVariable Long customerId,
            @Parameter(description = "Filter by payment status", example = "true")
            @RequestParam(required = false) Boolean isPaid,
            @Parameter(description = "Filter by number of installments", example = "12")
            @RequestParam(required = false) Integer numberOfInstallments) {
        List<LoanResponse> loans = loanService.getLoansByCustomerWithFilters(customerId, isPaid, numberOfInstallments);
        return ResponseEntity.ok(loans);
    }
}
