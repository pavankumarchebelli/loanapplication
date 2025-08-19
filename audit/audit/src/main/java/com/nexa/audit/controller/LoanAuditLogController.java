package com.nexa.audit.controller;

import com.nexa.audit.aop.annotations.AuditAction;
import com.nexa.audit.dto.*;
import com.nexa.audit.enums.LoanStatus;
import com.nexa.audit.service.LoanAuditLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loan-audit-logs")
public class LoanAuditLogController {

    private final LoanAuditLogService service;

    public LoanAuditLogController(LoanAuditLogService service) { this.service = service; }

    // List with filters
    @GetMapping
    public PageResponse<AuditLogResponse> list(
            @RequestParam(required = false) UUID loanId,
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) LoanStatus status,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort
    ) {
        return service.list(loanId, userId, status, from, to, page, size, sort);
    }

    // Get by id
    @GetMapping("/id")
    public AuditLogResponse get(@RequestParam UUID auditId) {
        return service.get(auditId);
    }

    // By Loan (timeline)
    @GetMapping("/by-loan")
    public List<AuditLogResponse> byLoan(@RequestParam UUID id) {
        return service.byLoan(id);
    }

    // Create (usually internal)
    @AuditAction("CREATE")
    @PostMapping
    public ResponseEntity<AuditLogResponse> create(@Valid @RequestBody CreateAuditLogRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    // Update (only updatedBy)
    @AuditAction("UPDATE")
    @PutMapping
    public ResponseEntity<AuditLogResponse> update(@Valid @RequestBody UpdateAuditLogRequest req) {
        return ResponseEntity.ok(service.update(req));
    }

    // Delete (admin only in real world)
    @AuditAction("DELETE")
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
