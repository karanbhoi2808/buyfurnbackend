package com.buyfurn.Buyfurn.service;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.buyfurn.Buyfurn.model.User;
import com.buyfurn.Buyfurn.model.UserListResponse;
import com.buyfurn.Buyfurn.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder encoder;

	public User createUser(User user) {
		user.setPasword(encoder.encode(user.getPasword()));
		user.setRoles(List.of("USER"));
		user.setAddress(user.getAddress());
		userRepository.save(user);
		return user;
	}

	public User updateUserRole(Long id, List<String> role) {
		User user = userRepository.findById(id).get();
		user.setRoles(role);
		userRepository.save(user);
		return user;
	}

	public List<User> getAll() {
		return userRepository.findAll();
	}

	public User getUser(String username) {
		return userRepository.findByEmail(username);
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
		User user=  userRepository.findByEmail(email);
		
		if(user!=null) {
			return true;
		}
		else
		{
			return false;
		}

	}

	public String deleteUser(Principal principal) {
		User user = userRepository.findByEmail(principal.getName());
		userRepository.delete(user);
		return "User Deleted";
	}

	 public User updateUser(User user) {
	        User existingUser = userRepository.findById(user.getId())
	                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

	        existingUser.setName(user.getName());
	        existingUser.setEmail(user.getEmail());
//	        existingUser.setPasword(encoder.encode(user.getPasword()));
//	        existingUser.setRoles(user.getRoles());
	        existingUser.setAddress(user.getAddress());
	        existingUser.setContactNumber(user.getContactNumber());

	        userRepository.save(existingUser);
	        return existingUser;
	    }

	public User getUserById(Long userId) {
		
		return userRepository.findById(userId).get();
	}

	public void updateUserCart(User user) {
		  userRepository.save(user);
	}

	public User updatePassword(User user) {
		String email=user.getEmail();
		User existingUser=userRepository.findByEmail(email);
		existingUser.setPasword(encoder.encode(user.getPasword()));
		userRepository.save(existingUser);
		return existingUser;
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
						u.getAddress(),
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
