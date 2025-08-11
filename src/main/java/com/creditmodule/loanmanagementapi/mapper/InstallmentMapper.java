package com.creditmodule.loanmanagementapi.mapper;

import com.creditmodule.loanmanagementapi.dto.response.InstallmentResponse;
import com.creditmodule.loanmanagementapi.entity.LoanInstallment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public class InstallmentMapper {
    public static InstallmentResponse toResponse(LoanInstallment installment) {
        InstallmentResponse response = new InstallmentResponse();
        response.setId(installment.getId());
        response.setLoanId(installment.getLoan().getId());
        response.setAmount(installment.getAmount());
        response.setPaidAmount(installment.getPaidAmount());
        response.setDueDate(installment.getDueDate());
        response.setPaymentDate(installment.getPaymentDate());
        response.setIsPaid(installment.getIsPaid());
        return response;
    }
}

