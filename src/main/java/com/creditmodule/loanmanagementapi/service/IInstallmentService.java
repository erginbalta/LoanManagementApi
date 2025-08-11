package com.creditmodule.loanmanagementapi.service;

import com.creditmodule.loanmanagementapi.dto.response.InstallmentResponse;
import com.creditmodule.loanmanagementapi.dto.request.PayInstallmentRequest;
import com.creditmodule.loanmanagementapi.dto.response.PayInstallmentResult;
import com.creditmodule.loanmanagementapi.entity.Loan;

import java.util.List;

public interface IInstallmentService {
    void generateInstallments(Loan loan);
    PayInstallmentResult payInstallment(PayInstallmentRequest request);
    List<InstallmentResponse> getInstallmentsByLoan(Long loanId);
    List<InstallmentResponse> getOverdueInstallments(Long loanId);

}
