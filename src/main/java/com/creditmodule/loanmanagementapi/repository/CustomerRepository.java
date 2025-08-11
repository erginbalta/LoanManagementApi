package com.creditmodule.loanmanagementapi.repository;

import com.creditmodule.loanmanagementapi.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}