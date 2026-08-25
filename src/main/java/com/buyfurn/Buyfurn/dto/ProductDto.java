package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
    private String title;
    private String description;
    private List<ProductImageDto> productImages;
    private double price;
    private String warranty;
    private String category;
    private String color;
    private String material;
    private int seatingCapacity;
    private double weight;
    private String careAndMaintenance;
    private String stockStatus;
    private LocalDateTime createdAt;
}
