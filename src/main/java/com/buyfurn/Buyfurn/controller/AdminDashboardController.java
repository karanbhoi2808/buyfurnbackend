package com.buyfurn.Buyfurn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyfurn.Buyfurn.dto.AdminDashboardResponse;
import com.buyfurn.Buyfurn.dto.ApiResponse;
import com.buyfurn.Buyfurn.service.AdminDashboardService;

import java.time.LocalDateTime;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {
    
    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/counts")
    public ApiResponse<AdminDashboardResponse> getAdminDashboardCounts() {
        AdminDashboardResponse data = adminDashboardService.getAdminDashboardCounts();
        return ApiResponse.<AdminDashboardResponse>builder()
                .success(true)
                .message("Admin dashboard counts retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
