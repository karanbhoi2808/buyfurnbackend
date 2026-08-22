package com.buyfurn.Buyfurn.model;

import java.util.List;

public class ProductPageResponse {
    private List<Product> products;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;

    public ProductPageResponse() {}

    public ProductPageResponse(List<Product> products, int currentPage, int totalPages, long totalElements, int pageSize) {
        this.products = products;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
