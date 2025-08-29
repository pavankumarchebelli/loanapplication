package com.nexa.loanapplication.dto.external;

import java.util.UUID;
import java.math.BigDecimal;

public class LoanPointDTO {
    private UUID loanPointId;
    private UUID loanTypeId;
    private BigDecimal maxLoanAmount;              // nullable
    private BigDecimal percentageOnLoanApproval;   // percent value e.g. 0.08 = 0.08%
    private Integer minCreditScore;
    private Integer maxCreditScore;

    // getters/setters...
    public UUID getLoanPointId(){return loanPointId;} public void setLoanPointId(UUID v){loanPointId=v;}
    public UUID getLoanTypeId(){return loanTypeId;} public void setLoanTypeId(UUID v){loanTypeId=v;}
    public BigDecimal getMaxLoanAmount(){return maxLoanAmount;} public void setMaxLoanAmount(BigDecimal v){maxLoanAmount=v;}
    public BigDecimal getPercentageOnLoanApproval(){return percentageOnLoanApproval;}
    public void setPercentageOnLoanApproval(BigDecimal v){percentageOnLoanApproval=v;}
    public Integer getMinCreditScore(){return minCreditScore;} public void setMinCreditScore(Integer v){minCreditScore=v;}
    public Integer getMaxCreditScore(){return maxCreditScore;} public void setMaxCreditScore(Integer v){maxCreditScore=v;}
}
