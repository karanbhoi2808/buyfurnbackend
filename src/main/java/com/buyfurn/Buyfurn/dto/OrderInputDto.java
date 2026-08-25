package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInputDto {
    @NotBlank(message = "Contact number cannot be blank")
    private String contactNumber;

    @Valid
    private AddressDto address;

    @NotEmpty(message = "Order quantities list cannot be empty")
    private List<@Valid OrderQuantityDto> orderQuantities;

    private String transactionId;
}
