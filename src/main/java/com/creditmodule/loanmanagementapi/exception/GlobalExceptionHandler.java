package com.creditmodule.loanmanagementapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Özel domain hataları
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFound(CustomerNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidInstallmentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInstallment(InvalidInstallmentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CreditLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleCreditLimitExceeded(CreditLimitExceededException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Genel iş hataları
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Geçersiz veri hataları
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid input: " + ex.getMessage());
    }

    // Genel fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }

    // Yardımcı method
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), status.value(), message);
        return new ResponseEntity<>(error, status);
    }
}

