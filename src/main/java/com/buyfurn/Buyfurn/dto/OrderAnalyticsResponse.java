package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAnalyticsResponse {
    private Summary summary;
    private List<MonthlyBreakdown> monthlyBreakdown;
    private List<CategoryBreakdown> categoryBreakdown;
    private Map<String, Integer> statusBreakdown;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private double totalRevenue;
        private int totalOrders;
        private double averageOrderValue;
        private int deliveredCount;
        private int placedCount;
        private PeakMonth peakMonth;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PeakMonth {
        private String month;
        private double revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyBreakdown {
        private String month;
        private int orderCount;
        private double totalPrice;
        private double avgOrderValue;
        private double percentage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryBreakdown {
        private String category;
        private int count;
        private double revenue;
    }
}
