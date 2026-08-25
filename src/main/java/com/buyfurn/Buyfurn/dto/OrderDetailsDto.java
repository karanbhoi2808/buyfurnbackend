package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailsDto {
    private long orderId;
    private String username;
    private AddressDto address;
    private String contact;
    private String orderStatus;
    private double amount;
    private String transactionId;
    private LocalDateTime createdAt;
    private ProductDto product;
    private Long userId;
}
