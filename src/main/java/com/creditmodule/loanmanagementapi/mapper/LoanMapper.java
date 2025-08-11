package com.creditmodule.loanmanagementapi.mapper;

import com.creditmodule.loanmanagementapi.dto.response.LoanResponse;
import com.creditmodule.loanmanagementapi.entity.Loan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class LoanMapper {
    public static LoanResponse toResponse(Loan loan) {
        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setCustomerId(loan.getCustomer().getId());
        response.setLoanAmount(loan.getLoanAmount());
        response.setNumberOfInstallment(loan.getNumberOfInstallments());
        response.setInterestRate(loan.getInterestRate());
        response.setCreateDate(loan.getCreateDate());
        response.setIsPaid(loan.getIsPaid());
        return response;
    }
}

