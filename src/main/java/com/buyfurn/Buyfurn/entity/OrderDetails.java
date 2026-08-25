package com.buyfurn.Buyfurn.entity;

import java.time.LocalDateTime;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long orderId;
    
    private String username;
    
    @Embedded
    private Address address;
    
    private String contact;
    
    @Column(name = "order_status")
    private String orderStatus;
    
    private double amount;
    
    @Column(name = "transaction_id")
    private String transactionId;

    @ManyToOne
    private Product product;

    @ManyToOne
    private User user;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public OrderDetails(String username, Address address, String contact, String orderStatus, double amount,
                        Product product, User user, String transactionId) {
        this.username = username;
        this.address = address;
        this.contact = contact;
        this.orderStatus = orderStatus;
        this.amount = amount;
        this.product = product;
        this.user = user;
        this.transactionId = transactionId;
    }
}
