package com.nexa.loanapplication.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public class CreateLoanApplicationRequest {
    @NotNull private UUID userId;
    @NotNull private UUID loanTypeId;
    @NotNull private UUID loanEid;         // eligibility rule chosen by front-end
    @NotNull @Positive private BigDecimal requestedAmount;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getLoanTypeId() { return loanTypeId; }
    public void setLoanTypeId(UUID loanTypeId) { this.loanTypeId = loanTypeId; }
    public UUID getLoanEid() { return loanEid; }
    public void setLoanEid(UUID loanEid) { this.loanEid = loanEid; }
    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }
}
