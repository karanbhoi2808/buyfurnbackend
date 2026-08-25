package com.buyfurn.Buyfurn.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.buyfurn.Buyfurn.dto.ApiResponse;
import com.buyfurn.Buyfurn.dto.CartDto;
import com.buyfurn.Buyfurn.service.CartService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/addToCart/{productId}/{quantity}")
    public ApiResponse<CartDto> addToCart(Principal principal, @PathVariable long productId, @PathVariable int quantity) {
        CartDto data = cartService.addToCart(principal, productId, quantity);
        return ApiResponse.<CartDto>builder()
                .success(true)
                .message("Product added to cart successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    @GetMapping("/getCartDetails")
    public ApiResponse<List<CartDto>> getCartDetails(Principal principal) {
        List<CartDto> data = cartService.getCartDetails(principal);
        return ApiResponse.<List<CartDto>>builder()
                .success(true)
                .message("Cart details retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
   
    @DeleteMapping("/deleteCartProduct/{cartId}")
    public ApiResponse<Void> deleteCartItem(@PathVariable Long cartId) {
        boolean isDeleted = cartService.deleteCartItem(cartId);
        
        if (isDeleted) {
            return ApiResponse.<Void>builder()
                    .success(true)
                    .message("Cart item deleted successfully")
                    .timestamp(LocalDateTime.now())
                    .build();
        } else {
            return ApiResponse.<Void>builder()
                    .success(false)
                    .message("Cart item not found")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
}
