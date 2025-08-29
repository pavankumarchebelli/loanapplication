package com.nexa.loanapplication.dto.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Mirrors your “conditional-approve” payload; we retain it in PROCESSING per the sheet. */
public class ConditionalApproveRequest {
    @NotNull private UUID loanId;
    @Min(0) @Max(100) private Double downPaymentPct; // optional policy hook
    private String notes;

    public UUID getLoanId() { return loanId; }
    public void setLoanId(UUID loanId) { this.loanId = loanId; }
    public Double getDownPaymentPct() { return downPaymentPct; }
    public void setDownPaymentPct(Double downPaymentPct) { this.downPaymentPct = downPaymentPct; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
