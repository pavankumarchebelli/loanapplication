package com.nexa.audit.dto;

import com.nexa.audit.enums.LoanStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response payload representing a Loan Audit Log entry.
 */
public record AuditLogResponse(
        UUID auditId,
        UUID loanId,
        UUID userId,
        LoanStatus status,
        String updatedBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
