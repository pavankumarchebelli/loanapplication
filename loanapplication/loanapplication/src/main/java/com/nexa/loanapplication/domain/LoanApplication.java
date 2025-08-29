package com.nexa.loanapplication.domain; // or .entity

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_applications", schema = "loanschema")
public class LoanApplication {

    @Id
    @Column(name = "loan_id", nullable = false)
    private UUID loanId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "loan_type_id", nullable = false)
    private UUID loanTypeId;

    @Column(name = "loan_eid", nullable = false)
    private UUID loanEid;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status;

    @Column(name = "requested_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "submitted_date")
    private java.sql.Date submittedDate; // per your table (DATE)

    @Column(name = "sanction_amount", precision = 12, scale = 2)
    private BigDecimal sanctionAmount;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @PrePersist
    void prePersist() {
        if (loanId == null) loanId = UUID.randomUUID();
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (status == null) status = LoanStatus.DRAFT;
    }

    public LoanApplication() {
    }

    public LoanApplication(UUID loanId, UUID userId, UUID loanTypeId, UUID loanEid, LoanStatus status, BigDecimal requestedAmount, Date submittedDate, BigDecimal sanctionAmount, OffsetDateTime createdAt, OffsetDateTime approvedAt) {
        this.loanId = loanId;
        this.userId = userId;
        this.loanTypeId = loanTypeId;
        this.loanEid = loanEid;
        this.status = status;
        this.requestedAmount = requestedAmount;
        this.submittedDate = submittedDate;
        this.sanctionAmount = sanctionAmount;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
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

    public Date getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Date submittedDate) {
        this.submittedDate = submittedDate;
    }

    public BigDecimal getSanctionAmount() {
        return sanctionAmount;
    }

    public void setSanctionAmount(BigDecimal sanctionAmount) {
        this.sanctionAmount = sanctionAmount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(OffsetDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
}
