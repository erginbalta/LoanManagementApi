package com.creditmodule.loanmanagementapi.controller;

import com.creditmodule.loanmanagementapi.config.SecurityConfig;
import com.creditmodule.loanmanagementapi.dto.request.CreateCustomerRequest;
import com.creditmodule.loanmanagementapi.dto.response.CustomerResponse;
import com.creditmodule.loanmanagementapi.service.ICustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(SecurityConfig.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICustomerService customerService;

    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "admin123";

    private final String CUSTOMER_USER = "customer";
    private final String CUSTOMER_PASS = "cust123";

    @Test
    void adminCanCreateCustomer() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("John");
        request.setSurname("Doe");
        request.setCreditLimit(new BigDecimal("10000"));

        CustomerResponse mockResponse = new CustomerResponse();
        mockResponse.setId(1L);
        mockResponse.setName("John");
        mockResponse.setSurname("Doe");
        mockResponse.setCreditLimit(new BigDecimal("10000"));
        mockResponse.setUsedCreditLimit(BigDecimal.ZERO);

        when(customerService.createCustomer(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/customers")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.creditLimit").value(10000))
                .andExpect(jsonPath("$.usedCreditLimit").value(0));
    }

    @Test
    void customerCannotCreateCustomer() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest();
        request.setName("John");
        request.setSurname("Doe");
        request.setCreditLimit(new BigDecimal("10000"));

        mockMvc.perform(post("/api/customers")
                        .with(httpBasic(CUSTOMER_USER, CUSTOMER_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanGetCustomerById() throws Exception {
        CustomerResponse mockResponse = new CustomerResponse();
        mockResponse.setId(1L);
        mockResponse.setName("John");
        mockResponse.setSurname("Doe");
        mockResponse.setCreditLimit(new BigDecimal("15000"));
        mockResponse.setUsedCreditLimit(BigDecimal.ZERO);

        when(customerService.getCustomerById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/customers/1")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.surname").value("Doe"));
    }

    @Test
    void customerCannotGetCustomerById() throws Exception {
        mockMvc.perform(get("/api/customers/1")
                        .with(httpBasic(CUSTOMER_USER, CUSTOMER_PASS)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanUpdateCreditLimit() throws Exception {
        CustomerResponse mockResponse = new CustomerResponse();
        mockResponse.setId(1L);
        mockResponse.setName("John");
        mockResponse.setSurname("Doe");
        mockResponse.setCreditLimit(new BigDecimal("15000"));
        mockResponse.setUsedCreditLimit(BigDecimal.ZERO);

        when(customerService.updateCreditLimit(eq(1L), eq(new BigDecimal("15000")))).thenReturn(mockResponse);

        mockMvc.perform(put("/api/customers/1/credit-limit")
                        .with(httpBasic(ADMIN_USER, ADMIN_PASS))
                        .param("newLimit", new BigDecimal("15000").toPlainString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditLimit").value(15000));
    }

    @Test
    void customerCannotUpdateCreditLimit() throws Exception {
        mockMvc.perform(put("/api/customers/1/credit-limit")
                        .with(httpBasic(CUSTOMER_USER, CUSTOMER_PASS))
                        .param("newLimit", new BigDecimal("15000").toPlainString()))
                .andExpect(status().isForbidden());
    }
}