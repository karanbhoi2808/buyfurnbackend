package com.buyfurn.Buyfurn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buyfurn.Buyfurn.entity.Cart;
import com.buyfurn.Buyfurn.entity.Product;
import com.buyfurn.Buyfurn.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    public List<Cart> findByUser(User user);
    public Optional<Cart> findByUserAndProduct(User user, Product product);
}
