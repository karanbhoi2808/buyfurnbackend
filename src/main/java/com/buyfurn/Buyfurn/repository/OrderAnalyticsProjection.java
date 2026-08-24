package com.buyfurn.Buyfurn.repository;

import java.time.LocalDateTime;

public interface OrderAnalyticsProjection {
    Double getAmount();
    String getOrderStatus();
    LocalDateTime getCreatedDate();
    String getCategory();
}
