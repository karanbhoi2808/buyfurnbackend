package com.buyfurn.Buyfurn.controller;

import com.buyfurn.Buyfurn.dto.ApiResponse;
import com.buyfurn.Buyfurn.dto.EmailRequest;
import com.buyfurn.Buyfurn.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public ApiResponse<Void> sendEmail(@Valid @RequestBody EmailRequest emailRequest) {
        emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getText());
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Email sent successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
