package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserListResponse {
    private long totalUsers;
    private long administratorsCount;
    private long customersCount;
    private int totalPages;
    private int currentPage;
    private List<UserResponseDTO> users;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserResponseDTO {
        private Long id;
        private String name;
        private String email;
        private List<String> roles;
        private AddressDto address;
        private String contactNumber;
    }
}
