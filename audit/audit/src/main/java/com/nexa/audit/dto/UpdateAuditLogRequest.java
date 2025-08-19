package com.nexa.audit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Request body for updating an existing Loan Audit Log entry's metadata.
 */
public record UpdateAuditLogRequest(
        @NotNull UUID auditId,
        @Size(max = 100) String updatedBy
) {}
