package com.buyfurn.Buyfurn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {
    private GrossRevenue grossRevenue;
    private TotalOrders totalOrders;
    private ActiveCatalogue activeCatalogue;
    private RegisteredAccounts registeredAccounts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GrossRevenue {
        private double amount;
        private long ordersProcessed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalOrders {
        private long total;
        private long pendingDispatch;
        private long delivered;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActiveCatalogue {
        private long liveItems;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisteredAccounts {
        private long total;
    }
}
