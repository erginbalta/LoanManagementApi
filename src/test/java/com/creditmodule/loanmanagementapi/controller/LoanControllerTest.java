package com.creditmodule.loanmanagementapi.controller;

import com.creditmodule.loanmanagementapi.config.SecurityConfig;
import com.creditmodule.loanmanagementapi.controller.LoanController;
import com.creditmodule.loanmanagementapi.dto.request.CreateLoanRequest;
import com.creditmodule.loanmanagementapi.dto.request.PayLoanRequest;
import com.creditmodule.loanmanagementapi.dto.response.LoanResponse;
import com.creditmodule.loanmanagementapi.dto.response.PaymentResult;
import com.creditmodule.loanmanagementapi.service.ILoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
@Import(SecurityConfig.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ILoanService loanService;

    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "admin123";

    private final String CUSTOMER_USER = "customer";
    private final String CUSTOMER_PASS = "cust123";

    @Test
    void customerCanCreateLoan() throws Exception {
        CreateLoanRequest request = new CreateLoanRequest();
        request.setCustomerId(1L);
        request.setAmount(new BigDecimal("5000"));
        request.setNumberOfInstallments("12");

        LoanResponse mockResponse = new LoanResponse();
        mockResponse.setId(1001L);
        mockResponse.setCustomerId(1L);
        mockResponse.setLoanAmount(new BigDecimal("5000"));
        mockResponse.setNumberOfInstallment(12);
        mockResponse.setInterestRate(new BigDecimal("0.2"));
        mockResponse.setCreateDate(LocalDate.of(2025, 8, 11));
        mockResponse.setIsPaid(false);


        when(loanService.createLoan(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/loans")
                        .with(httpBasic(CUSTOMER_USER, CUSTOMER_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.loanAmount").value(5000))
                .andExpect(jsonPath("$.numberOfInstallment").value(12))
                .andExpect(jsonPath("$.interestRate").value(0.2))
                .andExpect(jsonPath("$.createDate").value("2025-08-11"))
                .andExpect(jsonPath("$.isPaid").value(false));

    }

    @Test
    void customerCanPayLoan() throws Exception {
        PayLoanRequest request = new PayLoanRequest();
        request.setLoanId(1001L);
        request.setAmount(new BigDecimal("500"));

        PaymentResult result = new PaymentResult();
        result.setLoanFullyPaid(true);
        result.setInstallmentsPaid(3);
        result.setTotalAmountSpent(BigDecimal.valueOf(1500));

        when(loanService.payLoan(any())).thenReturn(result);

        mockMvc.perform(post("/api/loans/pay")
                        .with(httpBasic(CUSTOMER_USER, CUSTOMER_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Installment paid successfully"));
    }

    @Test
    void customerCanGetLoanDetails() throws Exception {
        LoanResponse mockResponse = new LoanResponse();
        mockResponse.setId(1001L);
        mockResponse.setCustomerId(1L);
        mockResponse.setLoanAmount(new BigDecimal("5000"));
        mockResponse.setNumberOfInstallment(12);
        mockResponse.setInterestRate(new BigDecimal("0.2"));
        mockResponse.setCreateDate(LocalDate.of(2025, 8, 11));
        mockResponse.setIsPaid(false);


        when(loanService.getLoanDetails(1001L)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/loans/1001")
                        .with(httpBasic(CUSTOMER_USER, CUSTOMER_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1001))
                .andExpect(jsonPath("$.remainingAmount").value(2500));
    }

    @Test
    void customerCanGetLoansByCustomer() throws Exception {
        LoanResponse mockResponse = new LoanResponse();
        mockResponse.setId(1001L);
        mockResponse.setCustomerId(1L);
        mockResponse.setLoanAmount(new BigDecimal("5000"));
        mockResponse.setNumberOfInstallment(12);
        mockResponse.setInterestRate(new BigDecimal("0.2"));
        mockResponse.setCreateDate(LocalDate.of(2025, 8, 11));
        mockResponse.setIsPaid(false);

        when(loanService.getLoansByCustomer(1L)).thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/api/loans/customer/1")
                        .with(httpBasic(CUSTOMER_USER, CUSTOMER_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1001));
    }

    @Test
    void customerCanGetFilteredLoans() throws Exception {
        LoanResponse mockResponse = new LoanResponse();
        mockResponse.setId(1001L);
        mockResponse.setCustomerId(1L);
        mockResponse.setLoanAmount(new BigDecimal("5000"));
        mockResponse.setNumberOfInstallment(12);
        mockResponse.setInterestRate(new BigDecimal("0.2"));
        mockResponse.setCreateDate(LocalDate.of(2025, 8, 11));
        mockResponse.setIsPaid(false);

        when(loanService.getLoansByCustomerWithFilters(1L, true, 12)).thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/api/loans/customer/1/filter")
                        .with(httpBasic(CUSTOMER_USER, CUSTOMER_PASS))
                        .param("isPaid", "true")
                        .param("numberOfInstallments", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1001));
    }
}