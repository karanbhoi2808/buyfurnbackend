package com.buyfurn.Buyfurn.model;

import java.time.LocalDateTime;
import java.util.List;

public class OrderListResponse {
    private long totalOrders;
    private long placedCount;
    private long deliveredCount;
    private double totalRevenue;
    private int totalPages;
    private int currentPage;
    private List<OrderResponseDTO> orders;

    public OrderListResponse() {}

    public OrderListResponse(long totalOrders, long placedCount, long deliveredCount, double totalRevenue,
                             int totalPages, int currentPage, List<OrderResponseDTO> orders) {
        this.totalOrders = totalOrders;
        this.placedCount = placedCount;
        this.deliveredCount = deliveredCount;
        this.totalRevenue = totalRevenue;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.orders = orders;
    }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public long getPlacedCount() { return placedCount; }
    public void setPlacedCount(long placedCount) { this.placedCount = placedCount; }

    public long getDeliveredCount() { return deliveredCount; }
    public void setDeliveredCount(long deliveredCount) { this.deliveredCount = deliveredCount; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public List<OrderResponseDTO> getOrders() { return orders; }
    public void setOrders(List<OrderResponseDTO> orders) { this.orders = orders; }

    public static class OrderResponseDTO {
        private long orderId;
        private String orderStatus;
        private LocalDateTime createdDate;
        private String contact;
        private UserResponseDTO user;
        private AddressResponseDTO address;
        private ProductResponseDTO product;

        public OrderResponseDTO() {}

        public OrderResponseDTO(long orderId, String orderStatus, LocalDateTime createdDate, String contact,
                                UserResponseDTO user, AddressResponseDTO address, ProductResponseDTO product) {
            this.orderId = orderId;
            this.orderStatus = orderStatus;
            this.createdDate = createdDate;
            this.contact = contact;
            this.user = user;
            this.address = address;
            this.product = product;
        }

        public long getOrderId() { return orderId; }
        public void setOrderId(long orderId) { this.orderId = orderId; }

        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

        public LocalDateTime getCreatedDate() { return createdDate; }
        public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

        public String getContact() { return contact; }
        public void setContact(String contact) { this.contact = contact; }

        public UserResponseDTO getUser() { return user; }
        public void setUser(UserResponseDTO user) { this.user = user; }

        public AddressResponseDTO getAddress() { return address; }
        public void setAddress(AddressResponseDTO address) { this.address = address; }

        public ProductResponseDTO getProduct() { return product; }
        public void setProduct(ProductResponseDTO product) { this.product = product; }
    }

    public static class UserResponseDTO {
        private String name;
        private String userName;
        private String email;

        public UserResponseDTO() {}

        public UserResponseDTO(String name, String userName, String email) {
            this.name = name;
            this.userName = userName;
            this.email = email;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class AddressResponseDTO {
        private String address;
        private String city;
        private String state;
        private String pincode;

        public AddressResponseDTO() {}

        public AddressResponseDTO(String address, String city, String state, String pincode) {
            this.address = address;
            this.city = city;
            this.state = state;
            this.pincode = pincode;
        }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getPincode() { return pincode; }
        public void setPincode(String pincode) { this.pincode = pincode; }
    }

    public static class ProductResponseDTO {
        private long id;
        private String title;
        private double price;
        private String category;
        private List<ProductImageResponseDTO> productImages;

        public ProductResponseDTO() {}

        public ProductResponseDTO(long id, String title, double price, String category, List<ProductImageResponseDTO> productImages) {
            this.id = id;
            this.title = title;
            this.price = price;
            this.category = category;
            this.productImages = productImages;
        }

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public List<ProductImageResponseDTO> getProductImages() { return productImages; }
        public void setProductImages(List<ProductImageResponseDTO> productImages) { this.productImages = productImages; }
    }

    public static class ProductImageResponseDTO {
        private String name;
        private String url;

        public ProductImageResponseDTO() {}

        public ProductImageResponseDTO(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
}
