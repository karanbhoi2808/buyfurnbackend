package com.buyfurn.Buyfurn.dto;

import com.buyfurn.Buyfurn.entity.*;
import java.util.List;
import java.util.stream.Collectors;

public class DtoMapper {

    public static AddressDto toDto(Address address) {
        if (address == null) return null;
        return new AddressDto(address.getAddress(), address.getPincode(), address.getCity(), address.getState());
    }

    public static Address toEntity(AddressDto dto) {
        if (dto == null) return null;
        return new Address(dto.getAddress(), dto.getPincode(), dto.getCity(), dto.getState());
    }

    public static UserDto toDto(User user) {
        if (user == null) return null;
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .address(toDto(user.getAddress()))
                .contactNumber(user.getContactNumber())
                .build();
    }

    public static ProductImageDto toDto(ProductImage img) {
        if (img == null) return null;
        return ProductImageDto.builder()
                .id(img.getId())
                .name(img.getName())
                .type(img.getType())
                .path(img.getPath())
                .url(img.getUrl())
                .sequence(img.getSequence())
                .build();
    }

    public static ProductDto toDto(Product product) {
        if (product == null) return null;
        return ProductDto.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .color(product.getColor())
                .material(product.getMaterial())
                .stockStatus(product.getStockStatus())
                .seatingCapacity(product.getSeatingCapacity())
                .warranty(product.getWarranty())
                .weight(product.getWeight())
                .careAndMaintenance(product.getCareAndMaintenance())
                .createdAt(product.getCreatedAt())
                .productImages(product.getProductImages() != null ? product.getProductImages().stream()
                        .map(DtoMapper::toDto)
                        .collect(Collectors.toList()) : List.of())
                .build();
    }

    public static CartDto toDto(Cart cart) {
        if (cart == null) return null;
        return CartDto.builder()
                .cartId(cart.getCartId())
                .quantity(cart.getQuantity())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .product(toDto(cart.getProduct()))
                .build();
    }

    public static OrderDetailsDto toDto(OrderDetails order) {
        if (order == null) return null;
        return OrderDetailsDto.builder()
                .orderId(order.getOrderId())
                .username(order.getUsername())
                .address(toDto(order.getAddress()))
                .contact(order.getContact())
                .orderStatus(order.getOrderStatus())
                .amount(order.getAmount())
                .transactionId(order.getTransactionId())
                .createdAt(order.getCreatedAt())
                .product(toDto(order.getProduct()))
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .build();
    }
}
