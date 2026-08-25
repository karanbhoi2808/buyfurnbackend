package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRequest {
    @NotBlank(message = "Recipient email cannot be blank")
    @Email(message = "Recipient email must be valid")
    private String to;

    @NotBlank(message = "Subject cannot be blank")
    private String subject;

    @NotBlank(message = "Body text cannot be blank")
    private String text;
}
