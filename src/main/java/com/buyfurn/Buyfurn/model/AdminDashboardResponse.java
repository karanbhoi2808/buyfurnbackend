package com.buyfurn.Buyfurn.model;

public class AdminDashboardResponse {
    private GrossRevenue grossRevenue;
    private TotalOrders totalOrders;
    private ActiveCatalogue activeCatalogue;
    private RegisteredAccounts registeredAccounts;

    public AdminDashboardResponse() {}

    public AdminDashboardResponse(GrossRevenue grossRevenue, TotalOrders totalOrders,
                                  ActiveCatalogue activeCatalogue, RegisteredAccounts registeredAccounts) {
        this.grossRevenue = grossRevenue;
        this.totalOrders = totalOrders;
        this.activeCatalogue = activeCatalogue;
        this.registeredAccounts = registeredAccounts;
    }

    public GrossRevenue getGrossRevenue() { return grossRevenue; }
    public void setGrossRevenue(GrossRevenue grossRevenue) { this.grossRevenue = grossRevenue; }

    public TotalOrders getTotalOrders() { return totalOrders; }
    public void setTotalOrders(TotalOrders totalOrders) { this.totalOrders = totalOrders; }

    public ActiveCatalogue getActiveCatalogue() { return activeCatalogue; }
    public void setActiveCatalogue(ActiveCatalogue activeCatalogue) { this.activeCatalogue = activeCatalogue; }

    public RegisteredAccounts getRegisteredAccounts() { return registeredAccounts; }
    public void setRegisteredAccounts(RegisteredAccounts registeredAccounts) { this.registeredAccounts = registeredAccounts; }

    public static class GrossRevenue {
        private double amount;
        private long ordersProcessed;

        public GrossRevenue() {}

        public GrossRevenue(double amount, long ordersProcessed) {
            this.amount = amount;
            this.ordersProcessed = ordersProcessed;
        }

        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }

        public long getOrdersProcessed() { return ordersProcessed; }
        public void setOrdersProcessed(long ordersProcessed) { this.ordersProcessed = ordersProcessed; }
    }

    public static class TotalOrders {
        private long total;
        private long pendingDispatch;
        private long delivered;

        public TotalOrders() {}

        public TotalOrders(long total, long pendingDispatch, long delivered) {
            this.total = total;
            this.pendingDispatch = pendingDispatch;
            this.delivered = delivered;
        }

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }

        public long getPendingDispatch() { return pendingDispatch; }
        public void setPendingDispatch(long pendingDispatch) { this.pendingDispatch = pendingDispatch; }

        public long getDelivered() { return delivered; }
        public void setDelivered(long delivered) { this.delivered = delivered; }
    }

    public static class ActiveCatalogue {
        private long liveItems;

        public ActiveCatalogue() {}

        public ActiveCatalogue(long liveItems) {
            this.liveItems = liveItems;
        }

        public long getLiveItems() { return liveItems; }
        public void setLiveItems(long liveItems) { this.liveItems = liveItems; }
    }

    public static class RegisteredAccounts {
        private long total;

        public RegisteredAccounts() {}

        public RegisteredAccounts(long total) {
            this.total = total;
        }

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
    }
}
