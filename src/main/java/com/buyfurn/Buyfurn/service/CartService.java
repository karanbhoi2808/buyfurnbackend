package com.buyfurn.Buyfurn.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buyfurn.Buyfurn.dto.CartDto;
import com.buyfurn.Buyfurn.dto.DtoMapper;
import com.buyfurn.Buyfurn.entity.Cart;
import com.buyfurn.Buyfurn.entity.Product;
import com.buyfurn.Buyfurn.entity.User;
import com.buyfurn.Buyfurn.exception.ResourceNotFoundException;
import com.buyfurn.Buyfurn.repository.CartRepository;
import com.buyfurn.Buyfurn.repository.ProductRepository;

@Service
public class CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserService userService;

    public CartDto addToCart(Principal principal, long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        String username = principal.getName();
        User user = userService.getUser(username);
        
        if (user != null) {
            Optional<Cart> existingCart = cartRepository.findByUserAndProduct(user, product);

            Cart cart;
            if (existingCart.isPresent()) {
                cart = existingCart.get();
                cart.setQuantity(cart.getQuantity() + quantity);
            } else {
                cart = new Cart(product, user);
                cart.setQuantity(quantity);
            }
            cartRepository.save(cart);
            return DtoMapper.toDto(cart);
        }

        throw new ResourceNotFoundException("User session not found");
    }
  
    public List<CartDto> getCartDetails(Principal principal){
        if (principal != null) {
            String username = principal.getName();
            User user = userService.getUser(username);
            if (user != null) {
                return cartRepository.findByUser(user).stream()
                        .map(DtoMapper::toDto)
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }

    public boolean deleteCartItem(Long cartId) {
        if (cartRepository.existsById(cartId)) {
            cartRepository.deleteById(cartId);
            return true; 
        }
        return false;
    }
}
