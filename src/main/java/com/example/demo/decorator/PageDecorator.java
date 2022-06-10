package com.example.demo.decorator;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.data.domain.Page;

public class PageDecorator<T> {
    
    private final Page<T> page;
    private int pageNumber;
    
    public PageDecorator(Page<T> page, int pageNumber) {
        this.page = page;
        this.pageNumber = pageNumber;
    }
    
    @JsonProperty("tasks") 
    public List<T> getContent() {
        return this.page.getContent();
    }

    @JsonProperty("counter") 
    public long getTotalElements() {
        return page.getTotalElements();
    }

    public long getNumberOfElements(){
        return page.getNumberOfElements();
    }

    public long getTotalPages(){
        return page.getTotalPages();
    }

    @JsonProperty("pageNumber") 
    public int getPageNumber(){
        return pageNumber;
    }
    
}