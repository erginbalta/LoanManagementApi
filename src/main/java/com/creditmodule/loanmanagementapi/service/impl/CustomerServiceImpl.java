package com.creditmodule.loanmanagementapi.service.impl;

import com.creditmodule.loanmanagementapi.dto.request.CreateCustomerRequest;
import com.creditmodule.loanmanagementapi.dto.response.CustomerResponse;
import com.creditmodule.loanmanagementapi.entity.Customer;
import com.creditmodule.loanmanagementapi.exception.CustomerNotFoundException;
import com.creditmodule.loanmanagementapi.mapper.CustomerMapper;
import com.creditmodule.loanmanagementapi.repository.CustomerRepository;
import com.creditmodule.loanmanagementapi.service.ICustomerService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Transactional
    public CustomerResponse updateCreditLimit(Long customerId, BigDecimal newLimit) {
        if (newLimit == null || newLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Credit limit must be greater than zero");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));

        customer.setCreditLimit(newLimit);
        Customer updatedCustomer = customerRepository.save(customer);

        return customerMapper.toResponse(updatedCustomer);
    }

}

