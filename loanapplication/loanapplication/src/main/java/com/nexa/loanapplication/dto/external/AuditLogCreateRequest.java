package com.nexa.loanapplication.dto.external;

import com.nexa.loanapplication.domain.LoanStatus;
import java.util.UUID;

/** POST body for /api/v1/loan-audit-logs (internal) */
public class AuditLogCreateRequest {
    private UUID loanId;
    private UUID userId;
    private LoanStatus status;
    private String updatedBy;

    public AuditLogCreateRequest() {}
    public AuditLogCreateRequest(UUID loanId, UUID userId, LoanStatus status, String updatedBy){
        this.loanId=loanId; this.userId=userId; this.status=status; this.updatedBy=updatedBy;
    }
    public UUID getLoanId(){return loanId;} public void setLoanId(UUID v){loanId=v;}
    public UUID getUserId(){return userId;} public void setUserId(UUID v){userId=v;}
    public LoanStatus getStatus(){return status;} public void setStatus(LoanStatus v){status=v;}
    public String getUpdatedBy(){return updatedBy;} public void setUpdatedBy(String v){updatedBy=v;}
}
