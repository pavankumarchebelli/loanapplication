package com.nexa.loanapplication.dto.external;

import java.util.UUID;
import java.math.BigDecimal;

/** Mirrors your "loan_eligibility_rules" row (read-only here) */
public class EligibilityRuleDTO {
    private UUID loanEid;
    private UUID loanTypeId;
    private BigDecimal maxLoanAmount; // nullable
    private BigDecimal apr;           // nullable when points-based
    private BigDecimal minSalary;
    private Integer minCreditScore;
    private Integer maxCreditScore;   // nullable
    private UUID loanPointId;         // null when direct-approve

    // getters/setters...
    public UUID getLoanEid(){return loanEid;} public void setLoanEid(UUID v){loanEid=v;}
    public UUID getLoanTypeId(){return loanTypeId;} public void setLoanTypeId(UUID v){loanTypeId=v;}
    public BigDecimal getMaxLoanAmount(){return maxLoanAmount;} public void setMaxLoanAmount(BigDecimal v){maxLoanAmount=v;}
    public BigDecimal getApr(){return apr;} public void setApr(BigDecimal v){apr=v;}
    public BigDecimal getMinSalary(){return minSalary;} public void setMinSalary(BigDecimal v){minSalary=v;}
    public Integer getMinCreditScore(){return minCreditScore;} public void setMinCreditScore(Integer v){minCreditScore=v;}
    public Integer getMaxCreditScore(){return maxCreditScore;} public void setMaxCreditScore(Integer v){maxCreditScore=v;}
    public UUID getLoanPointId(){return loanPointId;} public void setLoanPointId(UUID v){loanPointId=v;}
}
