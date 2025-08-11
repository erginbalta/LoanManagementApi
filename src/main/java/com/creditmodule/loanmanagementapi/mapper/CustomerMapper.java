package com.creditmodule.loanmanagementapi.mapper;

import com.creditmodule.loanmanagementapi.dto.request.CreateCustomerRequest;
import com.creditmodule.loanmanagementapi.dto.response.CustomerResponse;
import com.creditmodule.loanmanagementapi.entity.Customer;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public class CustomerMapper {
    public Customer toEntity(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setSurname(request.getSurname());
        customer.setCreditLimit(request.getCreditLimit());
        customer.setUsedCreditLimit(BigDecimal.ZERO); // yeni müşteri, hiç kredi kullanmamış
        return customer;
    }

    public CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setSurname(customer.getSurname());
        response.setCreditLimit(customer.getCreditLimit());
        response.setUsedCreditLimit(customer.getUsedCreditLimit());
        return response;
    }

}
