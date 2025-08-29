package com.nexa.loanapplication.dto.requests;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class SimpleLoanIdRequest {
    @NotNull private UUID loanId;
    public UUID getLoanId() { return loanId; }
    public void setLoanId(UUID loanId) { this.loanId = loanId; }
}
