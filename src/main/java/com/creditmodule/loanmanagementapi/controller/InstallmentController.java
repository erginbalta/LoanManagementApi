package com.creditmodule.loanmanagementapi.controller;

import com.creditmodule.loanmanagementapi.dto.request.PayInstallmentRequest;
import com.creditmodule.loanmanagementapi.dto.response.InstallmentResponse;
import com.creditmodule.loanmanagementapi.dto.response.PayInstallmentResult;
import com.creditmodule.loanmanagementapi.entity.Loan;
import com.creditmodule.loanmanagementapi.service.IInstallmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/installments")
@RequiredArgsConstructor
public class InstallmentController {

    private final IInstallmentService installmentService;

    @Operation(
            summary = "Generate installments for a loan",
            description = "Generates installment schedule based on loan details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Installments generated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid loan data")
            }
    )
    @PostMapping("/generate")
    public ResponseEntity<Void> generateInstallments(
            @Valid @RequestBody Loan loan) {
        installmentService.generateInstallments(loan);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Pay a specific installment",
            description = "Processes payment for a specific installment of a loan.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Installment paid successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid payment request"),
                    @ApiResponse(responseCode = "404", description = "Installment not found")
            }
    )
    @PostMapping("/pay")
    public ResponseEntity<PayInstallmentResult> payInstallment(
            @Valid @RequestBody PayInstallmentRequest request) {
        PayInstallmentResult result = installmentService.payInstallment(request);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Get all installments for a loan",
            description = "Returns all installments associated with a specific loan.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Installments retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Loan not found")
            }
    )
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<InstallmentResponse>> getInstallmentsByLoan(
            @Parameter(description = "Loan ID", example = "1001")
            @PathVariable Long loanId) {
        List<InstallmentResponse> installments = installmentService.getInstallmentsByLoan(loanId);
        return ResponseEntity.ok(installments);
    }

    @Operation(
            summary = "Get overdue installments for a loan",
            description = "Returns installments that are overdue for a specific loan.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Overdue installments retrieved"),
                    @ApiResponse(responseCode = "404", description = "Loan not found")
            }
    )
    @GetMapping("/loan/{loanId}/overdue")
    public ResponseEntity<List<InstallmentResponse>> getOverdueInstallments(
            @Parameter(description = "Loan ID", example = "1001")
            @PathVariable Long loanId) {
        List<InstallmentResponse> overdueInstallments = installmentService.getOverdueInstallments(loanId);
        return ResponseEntity.ok(overdueInstallments);
    }
}
