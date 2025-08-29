package com.nexa.loanapplication.dto.responses;

import java.util.List;

public class PageResponse<T> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private List<T> content;

    public PageResponse(int page,int size,long totalElements,int totalPages,List<T> content){
        this.page=page; this.size=size; this.totalElements=totalElements; this.totalPages=totalPages; this.content=content;
    }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }
    public List<T> getContent() { return content; }
}
