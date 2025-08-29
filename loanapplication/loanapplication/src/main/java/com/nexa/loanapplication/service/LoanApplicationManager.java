package com.nexa.loanapplication.service;

import com.nexa.loanapplication.config.ServicesProperties;
import com.nexa.loanapplication.domain.LoanApplication;
import com.nexa.loanapplication.domain.LoanStatus;
import com.nexa.loanapplication.dto.external.EligibilityRuleDTO;
import com.nexa.loanapplication.dto.external.LoanPointDTO;
import com.nexa.loanapplication.dto.external.UserDTO;
import com.nexa.loanapplication.dto.requests.*;
import com.nexa.loanapplication.dto.responses.LoanApplicationResponse;
import com.nexa.loanapplication.exception.BadRequestException;
import com.nexa.loanapplication.exception.ConflictException;
import com.nexa.loanapplication.exception.NotFoundException;
import com.nexa.loanapplication.external.*;
import com.nexa.loanapplication.repository.LoanApplicationRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class LoanApplicationManager {

    private final LoanApplicationRepository repo;
    private final EligibilityClient eligibilityClient;
    private final PointsClient pointsClient;
    private final AuditLogClient auditLogClient;

    private final RepaymentOptionsClient repaymentClient;

    private final UsersClient usersClient; // <-- NEW

    private final ServicesProperties props;

    public LoanApplicationManager(LoanApplicationRepository repo,
                                  EligibilityClient eligibilityClient,
                                  PointsClient pointsClient,
                                  AuditLogClient auditLogClient,
                                  RepaymentOptionsClient repaymentClient,
                                  UsersClient usersClient,
                                  ServicesProperties props) {
        this.repo = repo;
        this.eligibilityClient = eligibilityClient;
        this.pointsClient = pointsClient;
        this.auditLogClient = auditLogClient;
        this.repaymentClient = repaymentClient;
        this.usersClient = usersClient;                                 // <-- NEW
        this.props = props;
    }

    public Page<LoanApplicationResponse> list(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return repo.findAll(pageable).map(this::toResponse);
    }

    public LoanApplicationResponse getById(UUID loanId) {
        LoanApplication la = repo.findById(loanId).orElseThrow(() ->
                new NotFoundException("Loan not found: " + loanId));
        return toResponse(la);
    }

    /** POST /api/v1/loanapplications (create Draft) */
    public LoanApplicationResponse create(CreateLoanApplicationRequest in) {
        if (in.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("requestedAmount must be > 0");
        }

        // === NEW: verify user exists + baseline eligibility from Users service ===
        UserDTO user = usersClient.getById(in.getUserId()); // throws 404 -> bubbles as NotFound
        validateBaselineFromUser(user); // salary / creditScore quick screen

        // Resolve eligibility rule (from other service)
        var rule = eligibilityClient.getById(in.getLoanEid());
        if (!rule.getLoanTypeId().equals(in.getLoanTypeId())) {
            throw new BadRequestException("loanTypeId does not match eligibility rule's loanTypeId");
        }

        LoanApplication la = new LoanApplication();
        la.setLoanId(UUID.randomUUID());
        la.setUserId(in.getUserId());
        la.setLoanTypeId(in.getLoanTypeId());
        la.setLoanEid(in.getLoanEid());
        la.setRequestedAmount(in.getRequestedAmount());
        la.setStatus(LoanStatus.DRAFT);

        // we don't price yet; pricing occurs during approval
        la = repo.save(la);
        return toResponse(la);
    }

    /** POST /api/v1/loanapplications/submit  (Draft -> Submitted) */
    public LoanApplicationResponse submit(SimpleLoanIdRequest in, String actor) {
        var la = mustFind(in.getLoanId());
        ensureStatus(la, LoanStatus.DRAFT, "Only Draft can be submitted");
        la.setStatus(LoanStatus.SUBMITTED);
        la.setSubmittedDate(new java.sql.Date(System.currentTimeMillis()));
        repo.save(la);
        auditLogClient.writeAudit(la.getLoanId(), la.getUserId(), LoanStatus.SUBMITTED, actor);
        return toResponseWithComputedPricing(la); // will compute pricing fields as null here
    }


    /** POST /api/v1/loanapplications/start-processing (Submitted -> Processing) */
    public LoanApplicationResponse startProcessing(SimpleLoanIdRequest in, String actor) {
        LoanApplication la = mustFind(in.getLoanId());
        ensureStatus(la, LoanStatus.SUBMITTED, "Only Submitted can move to Processing");
        la.setStatus(LoanStatus.PROCESSING);
        repo.save(la);
        auditLogClient.writeAudit(la.getLoanId(), la.getUserId(), LoanStatus.PROCESSING, actor);
        return toResponse(la);
    }

    /** POST /api/v1/loanapplications/conditional-approve (stays in PROCESSING, sets hooks/notes) */
    public LoanApplicationResponse conditionalApprove(ConditionalApproveRequest in, String actor) {
        LoanApplication la = mustFind(in.getLoanId());
        ensureStatus(la, LoanStatus.PROCESSING, "Only Processing can be conditional-approved");

        // optional: store a small DP hook by recomputing financed principal during final approve
        // We keep status as PROCESSING, as per Excel.
        repo.save(la);
        auditLogClient.writeAudit(la.getLoanId(), la.getUserId(), LoanStatus.PROCESSING, actor);
        return toResponse(la);
    }

    /** POST /api/v1/loanapplications/approve (Processing -> Approved) */
    public LoanApplicationResponse approve(ApproveRequest in, String actor) {
        var la = mustFind(in.getLoanId());
        ensureStatus(la, LoanStatus.PROCESSING, "Only Processing can be Approved");
        if (in.getSanctionAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("sanctionAmount must be >= 0");
        }

        // Re-verify user exists + baseline (user might have been deleted/changed)
        UserDTO user = usersClient.getById(la.getUserId());
        validateBaselineFromUser(user);

        // Fetch rule
        var rule = eligibilityClient.getById(la.getLoanEid());

        // Compute pricing in-memory
        BigDecimal apr;
        UUID loanPointId = rule.getLoanPointId();
        BigDecimal pointsFee = BigDecimal.ZERO;
        boolean financeFee = true; // policy
        BigDecimal financedPrincipal = la.getRequestedAmount();

        if (loanPointId != null) {
            // points path -> APR 12.50 and compute fee
            apr = new BigDecimal("12.50");
            var lp = pointsClient.getById(loanPointId);
            var feeFraction = lp.getPercentageOnLoanApproval().divide(new BigDecimal("100"));
            pointsFee = la.getRequestedAmount().multiply(feeFraction).setScale(2, RoundingMode.HALF_UP);
            if (financeFee) {
                financedPrincipal = financedPrincipal.add(pointsFee);
            }
        } else {
            // direct approve uses rule APR
            if (rule.getApr() == null)
                throw new BadRequestException("Eligibility rule APR missing for direct-approve band");
            apr = rule.getApr().setScale(2, RoundingMode.HALF_UP);
        }

        // Persist only APPROVED columns
        la.setSanctionAmount(in.getSanctionAmount());
        la.setApprovedAt(OffsetDateTime.now());
        la.setStatus(LoanStatus.APPROVED);
        repo.save(la);

        auditLogClient.writeAudit(la.getLoanId(), la.getUserId(), LoanStatus.APPROVED, actor);

        // (NEW) Kick off repayment-options creation using computed figures
        // Choose tenure from policy or UI; for example, 36 months default:
        int months = 36; // or pull from request/policy
        repaymentClient.createRepaymentOptions(
                new RepaymentOptionsClient.ApprovedRepaymentCreateRequest(
                        la.getLoanId(), months, apr, financedPrincipal
                )
        );

        // Return response with computed pricing fields populated
        return toResponseWithComputedPricing(la, apr, loanPointId, pointsFee, financeFee, financedPrincipal);
    }

    /** POST /api/v1/loanapplications/reject (Processing -> Rejected) */
    public LoanApplicationResponse reject(SimpleLoanIdRequest in, String actor) {
        LoanApplication la = mustFind(in.getLoanId());
        ensureStatus(la, LoanStatus.PROCESSING, "Only Processing can be Rejected");
        la.setStatus(LoanStatus.REJECTED);
        repo.save(la);
        auditLogClient.writeAudit(la.getLoanId(), la.getUserId(), LoanStatus.REJECTED, actor);
        return toResponse(la);
    }

    /** POST /api/v1/loanapplications/accept (Approved -> Accepted) */
    public LoanApplicationResponse accept(SimpleLoanIdRequest in, String actor) {
        LoanApplication la = mustFind(in.getLoanId());
        ensureStatus(la, LoanStatus.APPROVED, "Only Approved can be Accepted");
        la.setStatus(LoanStatus.ACCEPTED);
        repo.save(la);
        auditLogClient.writeAudit(la.getLoanId(), la.getUserId(), LoanStatus.ACCEPTED, actor);
        return toResponse(la);
    }

    /** POST /api/v1/loanapplications/deny (Approved -> Denied) */
    public LoanApplicationResponse deny(SimpleLoanIdRequest in, String actor) {
        LoanApplication la = mustFind(in.getLoanId());
        ensureStatus(la, LoanStatus.APPROVED, "Only Approved can be Denied");
        la.setStatus(LoanStatus.DENIED);
        repo.save(la);
        auditLogClient.writeAudit(la.getLoanId(), la.getUserId(), LoanStatus.DENIED, actor);
        return toResponse(la);
    }

    /** DELETE /api/v1/loanapplications?id=<loanId> (Draft only) */
    public void deleteDraft(UUID loanId) {
        LoanApplication la = mustFind(loanId);
        ensureStatus(la, LoanStatus.DRAFT, "Only Draft can be deleted");
        repo.delete(la);
    }

    /** PUT /api/v1/loanapplications (update Draft only) */
    public LoanApplicationResponse updateDraft(UpdateDraftRequest in) {
        LoanApplication la = mustFind(in.getLoanId());
        ensureStatus(la, LoanStatus.DRAFT, "Only Draft can be updated");

        if (in.getRequestedAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("requestedAmount must be > 0");
        }
        la.setLoanTypeId(in.getLoanTypeId());
        la.setRequestedAmount(in.getRequestedAmount());
        repo.save(la);
        return toResponse(la);
    }

    // --- Helpers ---
    private LoanApplication mustFind(UUID id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Loan not found: " + id));
    }
    private void ensureStatus(LoanApplication la, LoanStatus required, String msg) {
        if (la.getStatus() != required) throw new ConflictException(msg);
    }
    private LoanApplicationResponse toResponse(LoanApplication la) {
        LoanApplicationResponse out = new LoanApplicationResponse();
        // copy persisted fields only
        out.setLoanId(la.getLoanId());
        out.setUserId(la.getUserId());
        out.setLoanTypeId(la.getLoanTypeId());
        out.setLoanEid(la.getLoanEid());
        out.setStatus(la.getStatus());
        out.setRequestedAmount(la.getRequestedAmount());
        out.setSanctionAmount(la.getSanctionAmount());
        out.setSubmittedDate(la.getSubmittedDate());
        out.setCreatedAt(la.getCreatedAt());
        out.setApprovedAt(la.getApprovedAt());
        return out;
    }

    private LoanApplicationResponse toResponseWithComputedPricing(LoanApplication la) {
        return toResponseWithComputedPricing(la, null, null, null, null, null);
    }

    private LoanApplicationResponse toResponseWithComputedPricing(
            LoanApplication la,
            BigDecimal apr,
            UUID loanPointId,
            BigDecimal pointsFee,
            Boolean financeFee,
            BigDecimal financedPrincipal) {

        LoanApplicationResponse out = toResponse(la);
        if (apr != null) out.setApr(apr);
        if (loanPointId != null) out.setLoanPointId(loanPointId);
        if (pointsFee != null) out.setPointsFee(pointsFee);
        if (financeFee != null) out.setPointsFinanced(financeFee);
        if (financedPrincipal != null) out.setFinancedPrincipal(financedPrincipal);
        return out;
    }

    private void validateBaselineFromUser(UserDTO user) {
        // Global baseline derived from your schema constraints:
        // - min_salary >= 2000 (global floor)
        // - credit_score between 500 and 850
        var salary = user.getSalary();
        var score = user.getCreditScore();

        if (salary == null || salary.compareTo(new BigDecimal("2000")) < 0) {
            throw new BadRequestException("User does not meet minimum salary requirement (>= 2000).");
        }
        if (score == null || score < 500 || score > 850) {
            throw new BadRequestException("User credit score must be between 500 and 850.");
        }
        // Optional: status check
        if (user.getStatus() != null && !"active".equalsIgnoreCase(user.getStatus())) {
            throw new BadRequestException("User is not active.");
        }
    }


}
