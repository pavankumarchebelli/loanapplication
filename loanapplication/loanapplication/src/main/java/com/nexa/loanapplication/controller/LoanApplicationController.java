package com.nexa.loanapplication.controller;

import com.nexa.loanapplication.dto.requests.*;
import com.nexa.loanapplication.dto.responses.LoanApplicationResponse;
import com.nexa.loanapplication.dto.responses.PageResponse;
import com.nexa.loanapplication.service.LoanApplicationManager;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loanapplications")
public class LoanApplicationController {

    private final LoanApplicationManager manager;

    public LoanApplicationController(LoanApplicationManager manager) {
        this.manager = manager;
    }

    // GET /api/v1/loanapplications
    @GetMapping
    public PageResponse<LoanApplicationResponse> list(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Page<LoanApplicationResponse> p = manager.list(page, size);
        return new PageResponse<>(p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.getContent());
    }

    // Excel shows: GET /api/v1/loanapplications/id?<loanId>
    // We also add a friendly /{loanId}
    @GetMapping("/id")
    public LoanApplicationResponse getByIdParam(@RequestParam("loanId") UUID loanId) {
        return manager.getById(loanId);
    }

    @GetMapping("/{loanId}")
    public LoanApplicationResponse getByIdPath(@PathVariable UUID loanId) {
        return manager.getById(loanId);
    }

    // POST /api/v1/loanapplications (create Draft)
    @PostMapping
    public ResponseEntity<LoanApplicationResponse> create(@Valid @RequestBody CreateLoanApplicationRequest in) {
        return ResponseEntity.ok(manager.create(in));
    }

    // POST /api/v1/loanapplications/submit
    @PostMapping("/submit")
    public ResponseEntity<?> submit(@Valid @RequestBody SimpleLoanIdRequest in,
                                    @RequestHeader(name = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(
                manager.submit(in, actor != null ? actor : "system")
        );
    }

    // POST /api/v1/loanapplications/start-processing
    @PostMapping("/start-processing")
    public ResponseEntity<?> startProcessing(@Valid @RequestBody SimpleLoanIdRequest in,
                                             @RequestHeader(name = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(
                manager.startProcessing(in, actor != null ? actor : "system")
        );
    }

    // POST /api/v1/loanapplications/conditional-approve
    @PostMapping("/conditional-approve")
    public ResponseEntity<?> conditionalApprove(@Valid @RequestBody ConditionalApproveRequest in,
                                                @RequestHeader(name = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(
                manager.conditionalApprove(in, actor != null ? actor : "system")
        );
    }

    // POST /api/v1/loanapplications/approve
    @PostMapping("/approve")
    public ResponseEntity<?> approve(@Valid @RequestBody ApproveRequest in,
                                     @RequestHeader(name = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(
                manager.approve(in, actor != null ? actor : "system")
        );
    }

    // POST /api/v1/loanapplications/reject
    @PostMapping("/reject")
    public ResponseEntity<?> reject(@Valid @RequestBody SimpleLoanIdRequest in,
                                    @RequestHeader(name = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(
                manager.reject(in, actor != null ? actor : "system")
        );
    }

    // POST /api/v1/loanapplications/accept
    @PostMapping("/accept")
    public ResponseEntity<?> accept(@Valid @RequestBody SimpleLoanIdRequest in,
                                    @RequestHeader(name = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(
                manager.accept(in, actor != null ? actor : "system")
        );
    }

    // POST /api/v1/loanapplications/deny
    @PostMapping("/deny")
    public ResponseEntity<?> deny(@Valid @RequestBody SimpleLoanIdRequest in,
                                  @RequestHeader(name = "X-Actor", required = false) String actor) {
        return ResponseEntity.ok(
                manager.deny(in, actor != null ? actor : "system")
        );
    }

    // DELETE /api/v1/loanapplications?id=<loanId>
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam("id") UUID loanId) {
        manager.deleteDraft(loanId);
        return ResponseEntity.noContent().build();
    }

    // PUT /api/v1/loanapplications (update Draft)
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody UpdateDraftRequest in) {
        return ResponseEntity.ok(manager.updateDraft(in));
    }
}
