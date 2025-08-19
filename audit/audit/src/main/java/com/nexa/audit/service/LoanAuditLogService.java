package com.nexa.audit.service;

import com.nexa.audit.dto.*;
import com.nexa.audit.entity.LoanAuditLog;
import com.nexa.audit.enums.LoanStatus;
import com.nexa.audit.exception.NotFoundException;
import com.nexa.audit.mapper.AuditLogMapper;
import com.nexa.audit.repository.LoanAuditLogRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class LoanAuditLogService {

    private final LoanAuditLogRepository repo;

    public LoanAuditLogService(LoanAuditLogRepository repo) {
        this.repo = repo;
    }

    public PageResponse<AuditLogResponse> list(UUID loanId, UUID userId, LoanStatus status,
                                               LocalDate from, LocalDate to,
                                               int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<LoanAuditLog> result;

        if (loanId != null && status != null) {
            result = repo.findAllByLoanIdAndStatus(loanId, status, pageable);
        } else if (loanId != null && from != null && to != null) {
            result = repo.findAllByLoanIdAndCreatedAtBetween(
                    loanId, startOfDay(from), endOfDay(to), pageable);
        } else if (loanId != null) {
            result = repo.findAllByLoanId(loanId, pageable);
        } else if (userId != null) {
            result = repo.findAllByUserId(userId, pageable);
        } else {
            result = repo.findAll(pageable);
        }
//        return toPageResponse(result);
        return toPageResponseDto(result);
    }

    public AuditLogResponse get(UUID auditId) {
        LoanAuditLog e = repo.findById(auditId)
                .orElseThrow(() -> new NotFoundException("Audit log not found"));
        return AuditLogMapper.toDto(e);
    }

    public List<AuditLogResponse> byLoan(UUID loanId) {
        return repo.findAllByLoanIdOrderByCreatedAtAsc(loanId)
                .stream().map(AuditLogMapper::toDto).toList();
    }

    public AuditLogResponse create(CreateAuditLogRequest req) {
        LoanAuditLog e = new LoanAuditLog();
        e.setLoanId(req.loanId());
        e.setUserId(req.userId());
        e.setStatus(req.status());
        e.setUpdatedBy(req.updatedBy());
        e = repo.save(e);
        return AuditLogMapper.toDto(e);
    }

    public AuditLogResponse update(UpdateAuditLogRequest req) {
        LoanAuditLog e = repo.findById(req.auditId())
                .orElseThrow(() -> new NotFoundException("Audit log not found"));
        if (req.updatedBy() != null) e.setUpdatedBy(req.updatedBy());
        e = repo.save(e);
        return AuditLogMapper.toDto(e);
    }

    public void delete(UUID auditId) {
        if (!repo.existsById(auditId)) throw new NotFoundException("Audit log not found");
        repo.deleteById(auditId);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "createdAt");
        String[] p = sort.split(",");
        return p.length == 2 ? Sort.by(Sort.Direction.fromString(p[1]), p[0])
                : Sort.by(Sort.Direction.DESC, p[0]);
    }

    private OffsetDateTime startOfDay(LocalDate d) { return d.atStartOfDay().atOffset(ZoneOffset.UTC); }
    private OffsetDateTime endOfDay(LocalDate d)   { return d.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC); }

//    private <T> PageResponse<T> toPageResponse(Page<T> page) {
//        return new PageResponse<>(page.getNumber(), page.getSize(), page.getTotalElements(),
//                page.getTotalPages(), page.getContent());
//    }

    private PageResponse<AuditLogResponse> toPageResponseDto(Page<LoanAuditLog> page) {
        var content = page.getContent()
                .stream()
                .map(AuditLogMapper::toDto)     // << entity -> DTO
                .toList();

        return new PageResponse<>(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                content
        );
    }

}
