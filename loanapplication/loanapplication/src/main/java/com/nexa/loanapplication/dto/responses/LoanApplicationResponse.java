package com.nexa.loanapplication.dto.responses;

import com.nexa.loanapplication.domain.LoanStatus;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.UUID;
public class LoanApplicationResponse {
    // persisted fields
    private UUID loanId; private UUID userId; private UUID loanTypeId; private UUID loanEid;
    private LoanStatus status; private BigDecimal requestedAmount; private BigDecimal sanctionAmount;
    private java.sql.Date submittedDate; private OffsetDateTime approvedAt; private OffsetDateTime createdAt;

    // computed (not stored)
    private BigDecimal apr;                 // e.g., 10.50 / 11.50 / 12.50
    private UUID loanPointId;               // if points path
    private BigDecimal pointsFee;           // computed from loan_points
    private BigDecimal financedPrincipal;   // requested + (fee if financed)
    private Boolean pointsFinanced;         // from policy

    // getters/setters…

    public LoanApplicationResponse() {
    }

    public LoanApplicationResponse(UUID loanId, UUID userId, UUID loanTypeId, UUID loanEid, LoanStatus status, BigDecimal requestedAmount, BigDecimal sanctionAmount, Date submittedDate, OffsetDateTime approvedAt, OffsetDateTime createdAt, BigDecimal apr, UUID loanPointId, BigDecimal pointsFee, BigDecimal financedPrincipal, Boolean pointsFinanced) {
        this.loanId = loanId;
        this.userId = userId;
        this.loanTypeId = loanTypeId;
        this.loanEid = loanEid;
        this.status = status;
        this.requestedAmount = requestedAmount;
        this.sanctionAmount = sanctionAmount;
        this.submittedDate = submittedDate;
        this.approvedAt = approvedAt;
        this.createdAt = createdAt;
        this.apr = apr;
        this.loanPointId = loanPointId;
        this.pointsFee = pointsFee;
        this.financedPrincipal = financedPrincipal;
        this.pointsFinanced = pointsFinanced;
    }

    public UUID getLoanId() {
        return loanId;
    }

    public void setLoanId(UUID loanId) {
        this.loanId = loanId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getLoanTypeId() {
        return loanTypeId;
    }

    public void setLoanTypeId(UUID loanTypeId) {
        this.loanTypeId = loanTypeId;
    }

    public UUID getLoanEid() {
        return loanEid;
    }

    public void setLoanEid(UUID loanEid) {
        this.loanEid = loanEid;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(BigDecimal requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public BigDecimal getSanctionAmount() {
        return sanctionAmount;
    }

    public void setSanctionAmount(BigDecimal sanctionAmount) {
        this.sanctionAmount = sanctionAmount;
    }

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public OffsetDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(OffsetDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getApr() {
        return apr;
    }

    public void setApr(BigDecimal apr) {
        this.apr = apr;
    }

    public UUID getLoanPointId() {
        return loanPointId;
    }

    public void setLoanPointId(UUID loanPointId) {
        this.loanPointId = loanPointId;
    }

    public BigDecimal getPointsFee() {
        return pointsFee;
    }

    public void setPointsFee(BigDecimal pointsFee) {
        this.pointsFee = pointsFee;
    }

    public BigDecimal getFinancedPrincipal() {
        return financedPrincipal;
    }

    public void setFinancedPrincipal(BigDecimal financedPrincipal) {
        this.financedPrincipal = financedPrincipal;
    }

    public Boolean getPointsFinanced() {
        return pointsFinanced;
    }

    public void setPointsFinanced(Boolean pointsFinanced) {
        this.pointsFinanced = pointsFinanced;
    }
}
