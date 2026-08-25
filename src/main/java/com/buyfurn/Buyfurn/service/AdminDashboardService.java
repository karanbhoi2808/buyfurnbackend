package com.buyfurn.Buyfurn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buyfurn.Buyfurn.dto.AdminDashboardResponse;
import com.buyfurn.Buyfurn.projection.OrderAnalyticsProjection;
import com.buyfurn.Buyfurn.repository.OrderDetailsRepository;
import com.buyfurn.Buyfurn.repository.ProductRepository;
import com.buyfurn.Buyfurn.repository.UserRepository;

@Service
public class AdminDashboardService {

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public AdminDashboardResponse getAdminDashboardCounts() {
        List<OrderAnalyticsProjection> allOrders = orderDetailsRepository.findAllAnalytics();
        double revenue = 0;
        long totalOrders = allOrders.size();
        long pendingCount = 0;
        long deliveredCount = 0;

        for (OrderAnalyticsProjection o : allOrders) {
            Double amt = o.getAmount();
            if (amt != null) {
                revenue += amt;
            }
            if (o.getOrderStatus() != null) {
                if (o.getOrderStatus().equalsIgnoreCase("Placed")) {
                    pendingCount++;
                } else if (o.getOrderStatus().equalsIgnoreCase("Delivered")) {
                    deliveredCount++;
                }
            }
        }

        double roundedRevenue = Math.round(revenue * 100.0) / 100.0;

        long liveItems = productRepository.count();
        long totalAccounts = userRepository.count();

        AdminDashboardResponse.GrossRevenue grossRevenue = new AdminDashboardResponse.GrossRevenue(roundedRevenue, totalOrders);
        AdminDashboardResponse.TotalOrders orders = new AdminDashboardResponse.TotalOrders(totalOrders, pendingCount, deliveredCount);
        AdminDashboardResponse.ActiveCatalogue catalogue = new AdminDashboardResponse.ActiveCatalogue(liveItems);
        AdminDashboardResponse.RegisteredAccounts accounts = new AdminDashboardResponse.RegisteredAccounts(totalAccounts);

        return new AdminDashboardResponse(grossRevenue, orders, catalogue, accounts);
    }
}
