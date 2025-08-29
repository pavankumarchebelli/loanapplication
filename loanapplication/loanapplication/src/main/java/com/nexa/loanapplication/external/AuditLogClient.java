package com.nexa.loanapplication.external;

import com.nexa.loanapplication.config.ServicesProperties;
import com.nexa.loanapplication.domain.LoanStatus;
import com.nexa.loanapplication.dto.external.AuditLogCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/** Calls POST /api/v1/loan-audit-logs (internal) */
@Component
public class AuditLogClient {
    private final RestTemplate rest;
    private final ServicesProperties props;

    public AuditLogClient(RestTemplate rest, ServicesProperties props) {
        this.rest = rest; this.props = props;
    }

    public void writeAudit(UUID loanId, UUID userId, LoanStatus newStatus, String updatedBy) {
        String url = props.getAudit().getBaseUrl() + "/api/v1/loan-audit-logs";
        AuditLogCreateRequest body = new AuditLogCreateRequest(loanId, userId, newStatus, updatedBy);
        ResponseEntity<Void> resp = rest.postForEntity(url, body, Void.class);
        // ignore body; rely on HTTP status for success
    }
}
