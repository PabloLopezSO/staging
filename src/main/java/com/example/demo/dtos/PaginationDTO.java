package com.example.demo.dtos;

import java.util.Optional;

public class PaginationDTO {
    
    private int pageNumber;
    private Optional<Integer>  pageSize;


    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return this.pageSize.get().intValue();
    }

    public void setPageSize(Optional<Integer>  pageSize) {
        this.pageSize = pageSize;
    }

}
