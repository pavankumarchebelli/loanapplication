package com.nexa.audit.dto;

import java.util.List;

/**
 * Generic pagination wrapper for API responses.
 *
 * @param <T> the element type contained in the page
 */
public record PageResponse<T>(
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<T> content
) {}
