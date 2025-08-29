package com.nexa.loanapplication.external;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.nexa.loanapplication.config.ServicesProperties;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class RepaymentOptionsClient {

    // If your Excel uses a different endpoint (e.g. "/api/v1/approved-loan-repayment-options"),
    // just change this one constant:
    private static final String BASE = "/api/v1/loanrepayments";

    private final RestTemplate rest;
    private final ServicesProperties props;

    public RepaymentOptionsClient(RestTemplate rest, ServicesProperties props) {
        this.rest = rest;
        this.props = props;
    }

    /** POST {repayment.base-url}/api/v1/loanrepayments */
    public void createRepaymentOptions(ApprovedRepaymentCreateRequest body) {
        String url = props.getRepayment().getBaseUrl() + BASE;
        rest.postForEntity(url, body, Void.class);
    }

    /** Optional helper: GET {repayment.base-url}/api/v1/loanrepayments?loanId=... */
    public ApprovedRepaymentOptionDTO[] listByLoanId(UUID loanId) {
        String url = props.getRepayment().getBaseUrl() + BASE + "?loanId=" + loanId;
        return rest.getForObject(url, ApprovedRepaymentOptionDTO[].class);
    }

    // ===== Payloads =====

    /** Creation request your loan-application will send to the repayment service */
    public static class ApprovedRepaymentCreateRequest {
        private UUID loanId;
        private Integer months;             // tenure
        private BigDecimal apr;             // computed APR (e.g., 12.50 for points path)
        private BigDecimal principal;       // financedPrincipal (requested + fee if financed)

        public ApprovedRepaymentCreateRequest() {}
        public ApprovedRepaymentCreateRequest(UUID loanId, Integer months, BigDecimal apr, BigDecimal principal) {
            this.loanId = loanId;
            this.months = months;
            this.apr = apr;
            this.principal = principal;
        }
        public UUID getLoanId() { return loanId; }
        public void setLoanId(UUID loanId) { this.loanId = loanId; }
        public Integer getMonths() { return months; }
        public void setMonths(Integer months) { this.months = months; }
        public BigDecimal getApr() { return apr; }
        public void setApr(BigDecimal apr) { this.apr = apr; }
        public BigDecimal getPrincipal() { return principal; }
        public void setPrincipal(BigDecimal principal) { this.principal = principal; }
    }

    /** Example response DTO if you choose to read them back */
    public static class ApprovedRepaymentOptionDTO {
        private UUID repaymentId;
        private UUID loanId;
        private Integer months;
        private BigDecimal apr;
        private BigDecimal monthlyPayments;
        private BigDecimal totalPayableAmount;

        public UUID getRepaymentId() { return repaymentId; }
        public void setRepaymentId(UUID repaymentId) { this.repaymentId = repaymentId; }
        public UUID getLoanId() { return loanId; }
        public void setLoanId(UUID loanId) { this.loanId = loanId; }
        public Integer getMonths() { return months; }
        public void setMonths(Integer months) { this.months = months; }
        public BigDecimal getApr() { return apr; }
        public void setApr(BigDecimal apr) { this.apr = apr; }
        public BigDecimal getMonthlyPayments() { return monthlyPayments; }
        public void setMonthlyPayments(BigDecimal monthlyPayments) { this.monthlyPayments = monthlyPayments; }
        public BigDecimal getTotalPayableAmount() { return totalPayableAmount; }
        public void setTotalPayableAmount(BigDecimal totalPayableAmount) { this.totalPayableAmount = totalPayableAmount; }
    }
}
