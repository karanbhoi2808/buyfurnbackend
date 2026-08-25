package com.buyfurn.Buyfurn.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.buyfurn.Buyfurn.dto.ApiResponse;
import com.buyfurn.Buyfurn.dto.OrderAnalyticsResponse;
import com.buyfurn.Buyfurn.dto.OrderDetailsDto;
import com.buyfurn.Buyfurn.dto.OrderInputDto;
import com.buyfurn.Buyfurn.dto.OrderListResponse;
import com.buyfurn.Buyfurn.dto.TransactionDetails;
import com.buyfurn.Buyfurn.service.OrderDetailService;
import jakarta.validation.Valid;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class OrderDetailsController {
    
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/user/placeOrder/{isSingleProductCheckout}")
    public ApiResponse<List<OrderDetailsDto>> placeOrder(
            Principal principal, 
            @Valid @RequestBody OrderInputDto orderInput, 
            @PathVariable boolean isSingleProductCheckout) {
        List<OrderDetailsDto> data = orderDetailService.placeOrder(orderInput, principal, isSingleProductCheckout);
        return ApiResponse.<List<OrderDetailsDto>>builder()
                .success(true)
                .message("Order placed successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("admin/allOrders")
    public ApiResponse<OrderListResponse> allOrders(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "searchKey", required = false) String searchKey,
            @RequestParam(value = "sortBy", defaultValue = "date") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        OrderListResponse data = orderDetailService.getAllOrders(
                status, searchKey, sortBy, sortDir, pageNumber, pageSize
        );
        return ApiResponse.<OrderListResponse>builder()
                .success(true)
                .message("All orders retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("admin/orders/analytics")
    public ApiResponse<OrderAnalyticsResponse> getOrderAnalytics(@RequestParam(value = "status", defaultValue = "all") String status) {
        OrderAnalyticsResponse data = orderDetailService.getOrderAnalytics(status);
        return ApiResponse.<OrderAnalyticsResponse>builder()
                .success(true)
                .message("Order analytics retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("user/myOrders")
    public ApiResponse<List<OrderDetailsDto>> myOrders(Principal principal) {
        List<OrderDetailsDto> data = orderDetailService.myOrders(principal);
        return ApiResponse.<List<OrderDetailsDto>>builder()
                .success(true)
                .message("User orders retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PutMapping("admin/markAsDelivered/{orderId}")
    public ApiResponse<OrderDetailsDto> markAsDelivered(@PathVariable long orderId) {
        OrderDetailsDto data = orderDetailService.markAsDelivered(orderId);
        return ApiResponse.<OrderDetailsDto>builder()
                .success(true)
                .message("Order marked as delivered successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("user/createTransaction/{amount}")
    public ApiResponse<TransactionDetails> createTransaction(@PathVariable double amount) {
        TransactionDetails data = orderDetailService.createTransaction(amount);
        return ApiResponse.<TransactionDetails>builder()
                .success(true)
                .message("Transaction created successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
