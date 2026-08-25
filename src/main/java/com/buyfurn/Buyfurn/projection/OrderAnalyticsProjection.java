package com.buyfurn.Buyfurn.projection;

import java.time.LocalDateTime;

public interface OrderAnalyticsProjection {
    Double getAmount();
    String getOrderStatus();
    LocalDateTime getCreatedAt();
    String getCategory();
}
