package com.nexa.audit.dto;

import com.nexa.audit.enums.LoanStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Request body for creating a new Loan Audit Log entry.
 */
public record CreateAuditLogRequest(
        @NotNull UUID loanId,
        @NotNull UUID userId,
        @NotNull LoanStatus status,
        @Size(max = 100) String updatedBy
) {}
