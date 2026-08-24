package com.buyfurn.Buyfurn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.buyfurn.Buyfurn.model.AdminDashboardResponse;
import com.buyfurn.Buyfurn.service.AdminDashboardService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {
    
    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/counts")
    public AdminDashboardResponse getAdminDashboardCounts() {
        return adminDashboardService.getAdminDashboardCounts();
    }
}
