package com.nexa.audit.mapper;

import com.nexa.audit.dto.AuditLogResponse;
import com.nexa.audit.entity.LoanAuditLog;

public final class AuditLogMapper {
    private AuditLogMapper() {}
    public static AuditLogResponse toDto(LoanAuditLog e) {
        return new AuditLogResponse(
                e.getAuditId(), e.getLoanId(), e.getUserId(), e.getStatus(),
                e.getUpdatedBy(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
