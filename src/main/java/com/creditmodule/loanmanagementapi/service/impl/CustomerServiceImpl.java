package com.creditmodule.loanmanagementapi.service.impl;

import com.creditmodule.loanmanagementapi.dto.request.CreateCustomerRequest;
import com.creditmodule.loanmanagementapi.dto.response.CustomerResponse;
import com.creditmodule.loanmanagementapi.entity.Customer;
import com.creditmodule.loanmanagementapi.exception.CustomerNotFoundException;
import com.creditmodule.loanmanagementapi.mapper.CustomerMapper;
import com.creditmodule.loanmanagementapi.repository.CustomerRepository;
import com.creditmodule.loanmanagementapi.service.ICustomerService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    
    @Autowired
    private final CustomerRepository customerRepository;

    @Autowired
    private final CustomerMapper customerMapper;

    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        logger.debug("Creating new customer with name: {}, surname: {}, credit limit: {}", 
                    request.getName(), request.getSurname(), request.getCreditLimit());
        
        Customer customer = customerMapper.toEntity(request);
        logger.debug("Mapped request to customer entity: ID will be auto-generated");
        
        Customer savedCustomer = customerRepository.save(customer);
        logger.debug("Saved customer with ID: {}, name: {} {}, credit limit: {}", 
                    savedCustomer.getId(), savedCustomer.getName(), savedCustomer.getSurname(), savedCustomer.getCreditLimit());
        
        CustomerResponse response = customerMapper.toResponse(savedCustomer);
        logger.debug("Successfully created customer with ID: {}", savedCustomer.getId());
        return response;
    }

    @Override
    public CustomerResponse getCustomerById(Long customerId) {
        logger.debug("Retrieving customer by ID: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.debug("Customer not found with ID: {}", customerId);
                    return new CustomerNotFoundException("Customer not found with id: " + customerId);
                });
        
        logger.debug("Found customer: ID: {}, name: {} {}, credit limit: {}, used credit: {}", 
                    customer.getId(), customer.getName(), customer.getSurname(), 
                    customer.getCreditLimit(), customer.getUsedCreditLimit());
        
        CustomerResponse response = customerMapper.toResponse(customer);
        logger.debug("Successfully retrieved customer with ID: {}", customerId);
        return response;
    }

    @Override
    @Transactional
    public CustomerResponse updateCreditLimit(Long customerId, BigDecimal newLimit) {
        logger.debug("Updating credit limit for customer ID: {} to new limit: {}", customerId, newLimit);
        
        if (newLimit == null || newLimit.compareTo(BigDecimal.ZERO) <= 0) {
            logger.debug("Invalid credit limit provided: {}", newLimit);
            throw new IllegalArgumentException("Credit limit must be greater than zero");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    logger.debug("Customer not found with ID: {} during credit limit update", customerId);
                    return new CustomerNotFoundException("Customer not found with id: " + customerId);
                });
        
        BigDecimal oldLimit = customer.getCreditLimit();
        logger.debug("Current credit limit for customer {}: {}, used credit: {}", 
                    customerId, oldLimit, customer.getUsedCreditLimit());

        customer.setCreditLimit(newLimit);
        Customer updatedCustomer = customerRepository.save(customer);
        logger.debug("Updated customer {} credit limit from {} to {}", 
                    customerId, oldLimit, newLimit);

        CustomerResponse response = customerMapper.toResponse(updatedCustomer);
        logger.debug("Successfully updated credit limit for customer ID: {}", customerId);
        return response;
    }
}