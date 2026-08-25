package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDetails {
    private String orderId;
    private String currency;
    private Integer amount;
    private String key;
}
