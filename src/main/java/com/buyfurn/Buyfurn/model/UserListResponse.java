package com.buyfurn.Buyfurn.model;

import java.util.List;

public class UserListResponse {
    private long totalUsers;
    private long administratorsCount;
    private long customersCount;
    private int totalPages;
    private int currentPage;
    private List<UserResponseDTO> users;

    public UserListResponse() {}

    public UserListResponse(long totalUsers, long administratorsCount, long customersCount,
                            int totalPages, int currentPage, List<UserResponseDTO> users) {
        this.totalUsers = totalUsers;
        this.administratorsCount = administratorsCount;
        this.customersCount = customersCount;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.users = users;
    }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getAdministratorsCount() { return administratorsCount; }
    public void setAdministratorsCount(long administratorsCount) { this.administratorsCount = administratorsCount; }

    public long getCustomersCount() { return customersCount; }
    public void setCustomersCount(long customersCount) { this.customersCount = customersCount; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public List<UserResponseDTO> getUsers() { return users; }
    public void setUsers(List<UserResponseDTO> users) { this.users = users; }

    public static class UserResponseDTO {
        private Long id;
        private String name;
        private String email;
        private List<String> roles;
        private Address address;
        private String contactNumber;

        public UserResponseDTO() {}

        public UserResponseDTO(Long id, String name, String email, List<String> roles, Address address, String contactNumber) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.roles = roles;
            this.address = address;
            this.contactNumber = contactNumber;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }

        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }

        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    }
}
