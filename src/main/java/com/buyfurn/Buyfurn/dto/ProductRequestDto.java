package com.buyfurn.Buyfurn.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductRequestDto {
    private Long id;

    @NotBlank(message = "Product title cannot be blank")
    private String title;

    @NotBlank(message = "Product description cannot be blank")
    private String description;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be a positive number")
    private Double price;

    private String warranty;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    private String color;
    private String material;
    private int seatingCapacity;
    private double weight;
    private String careAndMaintenance;

    @NotBlank(message = "Stock status cannot be blank")
    private String stockStatus;

    private List<ProductImageDto> productImages;
}
