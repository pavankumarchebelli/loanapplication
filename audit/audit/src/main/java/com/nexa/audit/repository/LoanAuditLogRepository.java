package com.nexa.audit.repository;

import com.nexa.audit.entity.LoanAuditLog;
import com.nexa.audit.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface LoanAuditLogRepository extends JpaRepository<LoanAuditLog, UUID> {

    Page<LoanAuditLog> findAllByLoanId(UUID loanId, Pageable pageable);

    Page<LoanAuditLog> findAllByUserId(UUID userId, Pageable pageable);

    Page<LoanAuditLog> findAllByLoanIdAndStatus(UUID loanId, LoanStatus status, Pageable pageable);

    List<LoanAuditLog> findAllByLoanIdOrderByCreatedAtAsc(UUID loanId);

    Page<LoanAuditLog> findAllByLoanIdAndCreatedAtBetween(UUID loanId,
                                                          OffsetDateTime from, OffsetDateTime to, Pageable pageable);
}
