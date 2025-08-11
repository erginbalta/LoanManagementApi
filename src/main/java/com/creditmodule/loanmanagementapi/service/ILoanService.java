package com.creditmodule.loanmanagementapi.service;

import com.creditmodule.loanmanagementapi.dto.request.CreateLoanRequest;
import com.creditmodule.loanmanagementapi.dto.request.PayLoanRequest;
import com.creditmodule.loanmanagementapi.dto.response.LoanResponse;
import com.creditmodule.loanmanagementapi.dto.response.PaymentResult;
import com.creditmodule.loanmanagementapi.entity.Loan;

import java.util.List;
import java.util.Optional;

public interface ILoanService {
    LoanResponse createLoan(CreateLoanRequest request);
    PaymentResult payLoan(PayLoanRequest request);
    LoanResponse getLoanDetails(Long loanId);
    List<LoanResponse> getLoansByCustomer(Long customerId);
    List<LoanResponse> getLoansByCustomerWithFilters(Long customerId, Boolean isPaid, Integer numberOfInstallments);
}
