package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderListResponse {
    private long totalOrders;
    private long placedCount;
    private long deliveredCount;
    private double totalRevenue;
    private int totalPages;
    private int currentPage;
    private List<OrderResponseDTO> orders;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderResponseDTO {
        private long orderId;
        private String orderStatus;
        private LocalDateTime createdAt;
        private String contact;
        private UserResponseDTO user;
        private AddressResponseDTO address;
        private ProductResponseDTO product;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserResponseDTO {
        private String name;
        private String userName;
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressResponseDTO {
        private String address;
        private String city;
        private String state;
        private String pincode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductResponseDTO {
        private long id;
        private String title;
        private double price;
        private String category;
        private List<ProductImageResponseDTO> productImages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductImageResponseDTO {
        private String name;
        private String url;
    }
}
