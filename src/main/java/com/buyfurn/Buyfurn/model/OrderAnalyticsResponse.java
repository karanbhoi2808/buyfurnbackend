package com.buyfurn.Buyfurn.model;

import java.util.List;
import java.util.Map;

public class OrderAnalyticsResponse {
    private Summary summary;
    private List<MonthlyBreakdown> monthlyBreakdown;
    private List<CategoryBreakdown> categoryBreakdown;
    private Map<String, Integer> statusBreakdown;

    public OrderAnalyticsResponse() {}

    public OrderAnalyticsResponse(Summary summary, List<MonthlyBreakdown> monthlyBreakdown,
                                  List<CategoryBreakdown> categoryBreakdown, Map<String, Integer> statusBreakdown) {
        this.summary = summary;
        this.monthlyBreakdown = monthlyBreakdown;
        this.categoryBreakdown = categoryBreakdown;
        this.statusBreakdown = statusBreakdown;
    }

    public Summary getSummary() { return summary; }
    public void setSummary(Summary summary) { this.summary = summary; }

    public List<MonthlyBreakdown> getMonthlyBreakdown() { return monthlyBreakdown; }
    public void setMonthlyBreakdown(List<MonthlyBreakdown> monthlyBreakdown) { this.monthlyBreakdown = monthlyBreakdown; }

    public List<CategoryBreakdown> getCategoryBreakdown() { return categoryBreakdown; }
    public void setCategoryBreakdown(List<CategoryBreakdown> categoryBreakdown) { this.categoryBreakdown = categoryBreakdown; }

    public Map<String, Integer> getStatusBreakdown() { return statusBreakdown; }
    public void setStatusBreakdown(Map<String, Integer> statusBreakdown) { this.statusBreakdown = statusBreakdown; }

    public static class Summary {
        private double totalRevenue;
        private int totalOrders;
        private double averageOrderValue;
        private int deliveredCount;
        private int placedCount;
        private PeakMonth peakMonth;

        public Summary() {}

        public Summary(double totalRevenue, int totalOrders, double averageOrderValue,
                       int deliveredCount, int placedCount, PeakMonth peakMonth) {
            this.totalRevenue = totalRevenue;
            this.totalOrders = totalOrders;
            this.averageOrderValue = averageOrderValue;
            this.deliveredCount = deliveredCount;
            this.placedCount = placedCount;
            this.peakMonth = peakMonth;
        }

        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

        public double getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(double averageOrderValue) { this.averageOrderValue = averageOrderValue; }

        public int getDeliveredCount() { return deliveredCount; }
        public void setDeliveredCount(int deliveredCount) { this.deliveredCount = deliveredCount; }

        public int getPlacedCount() { return placedCount; }
        public void setPlacedCount(int placedCount) { this.placedCount = placedCount; }

        public PeakMonth getPeakMonth() { return peakMonth; }
        public void setPeakMonth(PeakMonth peakMonth) { this.peakMonth = peakMonth; }
    }

    public static class PeakMonth {
        private String month;
        private double revenue;

        public PeakMonth() {}

        public PeakMonth(String month, double revenue) {
            this.month = month;
            this.revenue = revenue;
        }

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }

        public double getRevenue() { return revenue; }
        public void setRevenue(double revenue) { this.revenue = revenue; }
    }

    public static class MonthlyBreakdown {
        private String month;
        private int orderCount;
        private double totalPrice;
        private double avgOrderValue;
        private double percentage;

        public MonthlyBreakdown() {}

        public MonthlyBreakdown(String month, int orderCount, double totalPrice, double avgOrderValue, double percentage) {
            this.month = month;
            this.orderCount = orderCount;
            this.totalPrice = totalPrice;
            this.avgOrderValue = avgOrderValue;
            this.percentage = percentage;
        }

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }

        public int getOrderCount() { return orderCount; }
        public void setOrderCount(int orderCount) { this.orderCount = orderCount; }

        public double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

        public double getAvgOrderValue() { return avgOrderValue; }
        public void setAvgOrderValue(double avgOrderValue) { this.avgOrderValue = avgOrderValue; }

        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }

    public static class CategoryBreakdown {
        private String category;
        private int count;
        private double revenue;

        public CategoryBreakdown() {}

        public CategoryBreakdown(String category, int count, double revenue) {
            this.category = category;
            this.count = count;
            this.revenue = revenue;
        }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }

        public double getRevenue() { return revenue; }
        public void setRevenue(double revenue) { this.revenue = revenue; }
    }
}
