package com.nexa.loanapplication.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

/** For PUT /api/v1/loanapplications (update Draft only) */
public class UpdateDraftRequest {
    @NotNull private UUID loanId;
    @NotNull private UUID loanTypeId;
    @NotNull @Positive private BigDecimal requestedAmount;

    public UUID getLoanId() { return loanId; }
    public void setLoanId(UUID loanId) { this.loanId = loanId; }
    public UUID getLoanTypeId() { return loanTypeId; }
    public void setLoanTypeId(UUID loanTypeId) { this.loanTypeId = loanTypeId; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
}
