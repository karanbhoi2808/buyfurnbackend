package com.buyfurn.Buyfurn.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.buyfurn.Buyfurn.dto.ApiResponse;
import com.buyfurn.Buyfurn.dto.ProductDto;
import com.buyfurn.Buyfurn.dto.ProductPageResponse;
import com.buyfurn.Buyfurn.dto.ProductRequestDto;
import com.buyfurn.Buyfurn.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private Validator validator;

    @PostMapping(value = "/admin/add-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductDto> addProduct(@RequestPart("product") String productJson, @RequestPart("imgs") MultipartFile[] images) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequestDto productDto = objectMapper.readValue(productJson, ProductRequestDto.class);
        
        Set<ConstraintViolation<ProductRequestDto>> violations = validator.validate(productDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        ProductDto data = productService.addProduct(productDto, images);
        return ApiResponse.<ProductDto>builder()
                .success(true)
                .message("Product added successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/get-all-products")
    public ApiResponse<ProductPageResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(defaultValue = "") String searchKey,
            @RequestParam(required = false) List<String> searchCategory,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String stockStatus,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        ProductPageResponse data = productService.getAllProducts(pageNumber, pageSize, searchKey, searchCategory, minPrice, maxPrice, stockStatus, sortBy, sortDir);
        return ApiResponse.<ProductPageResponse>builder()
                .success(true)
                .message("Products retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/admin/get-all-products-for-admin")
    public ApiResponse<ProductPageResponse> getAllProductsForAdmin(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(defaultValue = "") String searchKey,
            @RequestParam(required = false) List<String> searchCategory,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String stockStatus,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        ProductPageResponse data = productService.getAllProducts(pageNumber, pageSize, searchKey, searchCategory, minPrice, maxPrice, stockStatus, sortBy, sortDir);
        return ApiResponse.<ProductPageResponse>builder()
                .success(true)
                .message("Products retrieved for admin successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/get-by-id/{id}")
    public ApiResponse<ProductDto> getById(@PathVariable Long id) {
        ProductDto data = productService.getById(id);
        return ApiResponse.<ProductDto>builder()
                .success(true)
                .message("Product retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping(value = "/admin/update-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductDto> updateProduct(@RequestPart("product") String prod, @RequestPart(value = "img", required = false) MultipartFile[] image) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ProductRequestDto productDto = objectMapper.readValue(prod, ProductRequestDto.class);
        
        Set<ConstraintViolation<ProductRequestDto>> violations = validator.validate(productDto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        ProductDto data = productService.updateProduct(productDto, image);
        return ApiResponse.<ProductDto>builder()
                .success(true)
                .message("Product updated successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @DeleteMapping("/admin/delete-by-id/{id}")
    public ApiResponse<Void> deleteById(@PathVariable Long id) {
        productService.deleteById(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Product Deleted !!")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/user/get-product-details/{isSingleProductCheckout}/{productId}")
    public ApiResponse<List<ProductDto>> getProductDetails(Principal principal, @PathVariable boolean isSingleProductCheckout, @PathVariable long productId) {
        List<ProductDto> data = productService.getProductDetails(principal, isSingleProductCheckout, productId);
        return ApiResponse.<List<ProductDto>>builder()
                .success(true)
                .message("Product details retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/latest")
    public ApiResponse<List<ProductDto>> getLatestProducts() {
        List<ProductDto> data = productService.getLatestProducts();
        return ApiResponse.<List<ProductDto>>builder()
                .success(true)
                .message("Latest products retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
