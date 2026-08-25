package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto {
    private long cartId;
    private ProductDto product;
    private Long userId;
    private int quantity;
}
