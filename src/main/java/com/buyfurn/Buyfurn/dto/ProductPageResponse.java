package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPageResponse {
    private List<ProductDto> products;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
}
