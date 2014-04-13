package com.ihtsdo.snomed.service;

import java.util.List;

public class Page<Type> {

    private List<Type> content;
    private long totalElements;
    
    public Page(List<Type> content, long totalElements) {
        this.content = content;
        this.totalElements = totalElements;
    }

    public List<Type> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

}
