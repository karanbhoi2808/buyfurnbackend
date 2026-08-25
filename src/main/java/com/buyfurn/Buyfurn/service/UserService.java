package com.buyfurn.Buyfurn.service;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.buyfurn.Buyfurn.dto.DtoMapper;
import com.buyfurn.Buyfurn.dto.UserDto;
import com.buyfurn.Buyfurn.dto.UserListResponse;
import com.buyfurn.Buyfurn.dto.UserRegistrationDto;
import com.buyfurn.Buyfurn.dto.UserUpdateDto;
import com.buyfurn.Buyfurn.entity.User;
import com.buyfurn.Buyfurn.exception.BadRequestException;
import com.buyfurn.Buyfurn.exception.ResourceNotFoundException;
import com.buyfurn.Buyfurn.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    public UserDto createUser(UserRegistrationDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new BadRequestException("Email already registered: " + userDto.getEmail());
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setRoles(List.of("USER"));
        user.setAddress(DtoMapper.toEntity(userDto.getAddress()));
        user.setContactNumber(userDto.getContactNumber());

        userRepository.save(user);
        return DtoMapper.toDto(user);
    }

    public UserDto updateUserRole(Long id, List<String> role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setRoles(role);
        userRepository.save(user);
        return DtoMapper.toDto(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getUser(String username) {
        return userRepository.findByEmail(username);
    }

    public UserDto getUserDto(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + username);
        }
        return DtoMapper.toDto(user);
    }
    
    private final Map<String, String> otps = new HashMap<>();
    private final SecureRandom random = new SecureRandom();

    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + random.nextInt(900000)); 
        otps.put(email, otp);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String storedOtp = otps.get(email);

        if (storedOtp != null && storedOtp.equals(otp)) {
            otps.remove(email); 
            return true;
        }
        return false;
    }

    public void clearOtp(String email) {
        otps.remove(email);
    }

    public boolean verifyEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    public String deleteUser(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.delete(user);
        return "User Deleted";
    }

    public UserDto updateUser(UserUpdateDto userDto) {
        User existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userDto.getId()));

        // Check email uniqueness if email is changed
        if (!existingUser.getEmail().equalsIgnoreCase(userDto.getEmail())) {
            if (userRepository.findByEmail(userDto.getEmail()) != null) {
                throw new BadRequestException("Email already in use: " + userDto.getEmail());
            }
        }

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setAddress(DtoMapper.toEntity(userDto.getAddress()));
        existingUser.setContactNumber(userDto.getContactNumber());

        userRepository.save(existingUser);
        return DtoMapper.toDto(existingUser);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    public UserDto getUserDtoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return DtoMapper.toDto(user);
    }

    public void updateUserCart(User user) {
        userRepository.save(user);
    }

    public UserDto updatePassword(UserDto userDto, String newPassword) {
        User existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser == null) {
            throw new ResourceNotFoundException("User not found with email: " + userDto.getEmail());
        }
        existingUser.setPassword(encoder.encode(newPassword));
        userRepository.save(existingUser);
        return DtoMapper.toDto(existingUser);
    }

    public UserListResponse getAllUsers(
            String searchKey,
            String role,
            String sortBy,
            String sortDir,
            int pageNumber,
            int pageSize) {

        List<User> allUsers = userRepository.findAll();

        long totalUsers = allUsers.size();
        long administratorsCount = 0;
        long customersCount = 0;

        for (User u : allUsers) {
            boolean isAdmin = u.getRoles() != null && u.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
            boolean isCustomer = u.getRoles() != null && u.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("USER"));
            if (isAdmin) {
                administratorsCount++;
            }
            if (isCustomer) {
                customersCount++;
            }
        }

        List<User> filteredUsers = new java.util.ArrayList<>();
        if (searchKey != null && !searchKey.trim().isEmpty()) {
            String query = searchKey.trim().toLowerCase();
            for (User u : allUsers) {
                boolean nameMatch = u.getName() != null && u.getName().toLowerCase().contains(query);
                boolean emailMatch = u.getEmail() != null && u.getEmail().toLowerCase().contains(query);
                if (nameMatch || emailMatch) {
                    filteredUsers.add(u);
                }
            }
        } else {
            filteredUsers.addAll(allUsers);
        }

        if (role != null && !role.trim().isEmpty() && !role.equalsIgnoreCase("all")) {
            String roleQuery = role.trim().toLowerCase();
            List<User> temp = new java.util.ArrayList<>();
            for (User u : filteredUsers) {
                boolean roleMatch = u.getRoles() != null && u.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase(roleQuery));
                if (roleMatch) {
                    temp.add(u);
                }
            }
            filteredUsers = temp;
        }

        if ("admin".equalsIgnoreCase(sortBy) || "adminFirst".equalsIgnoreCase(sortBy)) {
            filteredUsers.sort((u1, u2) -> {
                boolean isAdmin1 = u1.getRoles() != null && u1.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
                boolean isAdmin2 = u2.getRoles() != null && u2.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN"));
                if (isAdmin1 && !isAdmin2) {
                    return -1;
                } else if (!isAdmin1 && isAdmin2) {
                    return 1;
                } else {
                    String name1 = u1.getName() != null ? u1.getName() : "";
                    String name2 = u2.getName() != null ? u2.getName() : "";
                    return name1.compareToIgnoreCase(name2);
                }
            });
        } else {
            filteredUsers.sort((u1, u2) -> {
                String name1 = u1.getName() != null ? u1.getName() : "";
                String name2 = u2.getName() != null ? u2.getName() : "";
                return name1.compareToIgnoreCase(name2);
            });
        }

        if ("desc".equalsIgnoreCase(sortDir)) {
            java.util.Collections.reverse(filteredUsers);
        }

        int totalFiltered = filteredUsers.size();
        int totalPages = (int) Math.ceil((double) totalFiltered / pageSize);
        if (totalPages == 0) {
            totalPages = 1;
        }

        int startIdx = pageNumber * pageSize;
        int endIdx = Math.min(startIdx + pageSize, totalFiltered);

        List<UserListResponse.UserResponseDTO> dtoList = new java.util.ArrayList<>();
        if (startIdx < totalFiltered) {
            List<User> pageContent = filteredUsers.subList(startIdx, endIdx);
            for (User u : pageContent) {
                dtoList.add(new UserListResponse.UserResponseDTO(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getRoles(),
                        DtoMapper.toDto(u.getAddress()),
                        u.getContactNumber()
                ));
            }
        }

        return new UserListResponse(
                totalUsers,
                administratorsCount,
                customersCount,
                totalPages,
                pageNumber,
                dtoList
        );
    }
}
