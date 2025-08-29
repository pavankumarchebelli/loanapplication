package com.nexa.loanapplication.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public class ApproveRequest {
    @NotNull private UUID loanId;
    @NotNull @Positive private BigDecimal sanctionAmount;
    private String notes;

    public UUID getLoanId() { return loanId; }
    public void setLoanId(UUID loanId) { this.loanId = loanId; }
    public BigDecimal getSanctionAmount() { return sanctionAmount; }
    public void setSanctionAmount(BigDecimal sanctionAmount) { this.sanctionAmount = sanctionAmount; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
