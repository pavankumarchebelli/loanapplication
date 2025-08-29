package com.nexa.loanapplication.repository;

import com.nexa.loanapplication.domain.LoanApplication;
import com.nexa.loanapplication.domain.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
    Page<LoanApplication> findAllByStatus(LoanStatus status, Pageable pageable);
}
