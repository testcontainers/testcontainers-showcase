package org.testcontainers.bookstore.catalog.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.domain.Page;

public class PagedResult<T> {
    private List<T> data;
    private long totalElements;
    private int pageNumber;
    private int totalPages;

    @JsonProperty("isFirst")
    private boolean isFirst;

    @JsonProperty("isLast")
    private boolean isLast;

    @JsonProperty("hasNext")
    private boolean hasNext;

    @JsonProperty("hasPrevious")
    private boolean hasPrevious;

    public PagedResult(Page<T> page) {
        this.setData(page.getContent());
        this.setTotalElements(page.getTotalElements());
        this.setPageNumber(page.getNumber() + 1); // 1 - based page numbering
        this.setTotalPages(page.getTotalPages());
        this.setFirst(page.isFirst());
        this.setLast(page.isLast());
        this.setHasNext(page.hasNext());
        this.setHasPrevious(page.hasPrevious());
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
