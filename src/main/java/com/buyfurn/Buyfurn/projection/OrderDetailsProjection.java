package com.buyfurn.Buyfurn.projection;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDetailsProjection {
    long getOrderId();
    String getOrderStatus();
    LocalDateTime getCreatedAt();
    String getContact();
    String getUsername();
    Double getAmount();

    UserProj getUser();
    AddressProj getAddress();
    ProductProj getProduct();

    interface UserProj {
        String getName();
        String getEmail();
    }

    interface AddressProj {
        String getAddress();
        String getCity();
        String getState();
        String getPincode();
    }

    interface ProductProj {
        long getId();
        String getTitle();
        double getPrice();
        String getCategory();
        List<ProductImageProj> getProductImages();
    }

    interface ProductImageProj {
        String getName();
        String getUrl();
    }
}
