package com.buyfurn.Buyfurn.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.buyfurn.Buyfurn.dto.ApiResponse;
import com.buyfurn.Buyfurn.dto.UserDto;
import com.buyfurn.Buyfurn.dto.UserListResponse;
import com.buyfurn.Buyfurn.dto.UserRegistrationDto;
import com.buyfurn.Buyfurn.dto.UserUpdateDto;
import com.buyfurn.Buyfurn.exception.BadRequestException;
import com.buyfurn.Buyfurn.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserDto> registerUser(@Valid @RequestBody UserRegistrationDto userDto) {
        UserDto data = userService.createUser(userDto);
        return ApiResponse.<UserDto>builder()
                .success(true)
                .message("User registered successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/updatepassword")
    public ApiResponse<Void> updatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        if (email == null || password == null) {
            throw new BadRequestException("Email and password are required");
        }
        UserDto userDto = UserDto.builder().email(email).build();
        userService.updatePassword(userDto, password);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Password updated successfully")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/_updateuserrole/{id}/{role}")
    public ApiResponse<UserDto> updateUserRole(@PathVariable Long id, @PathVariable List<String> role) {
        UserDto data = userService.updateUserRole(id, role);
        return ApiResponse.<UserDto>builder()
                .success(true)
                .message("User role updated successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/login")
    public ApiResponse<UserDto> getLogin(Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            UserDto data = userService.getUserDto(username);
            return ApiResponse.<UserDto>builder()
                    .success(true)
                    .message("User logged in successfully")
                    .data(data)
                    .timestamp(LocalDateTime.now())
                    .build();
        } else {
            return ApiResponse.<UserDto>builder()
                    .success(false)
                    .message("No active session found")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    @GetMapping("/getall")
    public ApiResponse<UserListResponse> getAll(
            @RequestParam(value = "searchKey", required = false) String searchKey,
            @RequestParam(value = "role", defaultValue = "all") String role,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        UserListResponse data = userService.getAllUsers(searchKey, role, sortBy, sortDir, pageNumber, pageSize);
        return ApiResponse.<UserListResponse>builder()
                .success(true)
                .message("Users retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/generate-otp")
    public ApiResponse<String> generateOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null) {
            throw new BadRequestException("Email is required");
        }
        String otp = userService.generateOtp(email);
        boolean isVerified = userService.verifyEmail(email);

        return ApiResponse.<String>builder()
                .success(isVerified)
                .message(isVerified ? "Email already exists" : "OTP generated successfully")
                .data(otp)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<Boolean> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        if (email == null || otp == null) {
            throw new BadRequestException("Email and OTP are required");
        }
        boolean isValid = userService.validateOtp(email, otp);
        return ApiResponse.<Boolean>builder()
                .success(isValid)
                .message(isValid ? "OTP verified successfully" : "Invalid OTP")
                .data(isValid)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @DeleteMapping("/user/delete")
    public ApiResponse<String> deleteUser(Principal principal) {
        String data = userService.deleteUser(principal);
        return ApiResponse.<String>builder()
                .success(true)
                .message(data)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @PostMapping(value = "/user/updateuser")
    public ApiResponse<UserDto> updateUser(@Valid @RequestBody UserUpdateDto userDto) throws IOException {
        UserDto data = userService.updateUser(userDto);
        return ApiResponse.<UserDto>builder()
                .success(true)
                .message("User profile updated successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @GetMapping("/user/getByEmail/{email}")
    public ApiResponse<UserDto> getByEmail(@PathVariable String email) {
        UserDto data = userService.getUserDto(email);
        return ApiResponse.<UserDto>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
