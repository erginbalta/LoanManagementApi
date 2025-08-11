package com.creditmodule.loanmanagementapi.service;

import com.creditmodule.loanmanagementapi.dto.request.CreateCustomerRequest;
import com.creditmodule.loanmanagementapi.dto.response.CustomerResponse;
import com.creditmodule.loanmanagementapi.entity.Customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ICustomerService {
    CustomerResponse createCustomer(CreateCustomerRequest request);
    CustomerResponse getCustomerById(Long customerId);
    CustomerResponse updateCreditLimit(Long customerId, BigDecimal newLimit);
}

