package com.buyfurn.Buyfurn.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.buyfurn.Buyfurn.model.OrderAnalyticsResponse;
import com.buyfurn.Buyfurn.repository.OrderAnalyticsProjection;
import com.buyfurn.Buyfurn.repository.OrderDetailsRepository;

@ExtendWith(MockitoExtension.class)
public class OrderDetailServiceTest {

    @Mock
    private OrderDetailsRepository orderDetailsRepository;

    @InjectMocks
    private OrderDetailService orderDetailService;

    @Test
    public void testGetOrderAnalytics_All() {
        // Setup mock projections
        OrderAnalyticsProjection o1 = mock(OrderAnalyticsProjection.class);
        when(o1.getAmount()).thenReturn(100.0);
        when(o1.getOrderStatus()).thenReturn("Delivered");
        when(o1.getCreatedDate()).thenReturn(LocalDateTime.of(2024, 8, 15, 12, 0));
        when(o1.getCategory()).thenReturn("Living Room");

        OrderAnalyticsProjection o2 = mock(OrderAnalyticsProjection.class);
        when(o2.getAmount()).thenReturn(50.0);
        when(o2.getOrderStatus()).thenReturn("Placed");
        when(o2.getCreatedDate()).thenReturn(LocalDateTime.of(2024, 8, 20, 12, 0));
        when(o2.getCategory()).thenReturn("Bedroom");

        OrderAnalyticsProjection o3 = mock(OrderAnalyticsProjection.class);
        when(o3.getAmount()).thenReturn(200.0);
        when(o3.getOrderStatus()).thenReturn("Placed");
        when(o3.getCreatedDate()).thenReturn(LocalDateTime.of(2024, 9, 10, 12, 0));
        when(o3.getCategory()).thenReturn("Living Room");

        List<OrderAnalyticsProjection> mockOrders = Arrays.asList(o1, o2, o3);
        when(orderDetailsRepository.findAllAnalytics()).thenReturn(mockOrders);

        // Run the service logic
        OrderAnalyticsResponse response = orderDetailService.getOrderAnalytics("all");

        // Verify summary
        assertNotNull(response);
        assertEquals(350.0, response.getSummary().getTotalRevenue());
        assertEquals(3, response.getSummary().getTotalOrders());
        assertEquals(116.67, response.getSummary().getAverageOrderValue());
        assertEquals(1, response.getSummary().getDeliveredCount());
        assertEquals(2, response.getSummary().getPlacedCount());
        assertEquals("Sep 2024", response.getSummary().getPeakMonth().getMonth());
        assertEquals(200.0, response.getSummary().getPeakMonth().getRevenue());

        // Verify monthly breakdown
        assertEquals(2, response.getMonthlyBreakdown().size());
        
        OrderAnalyticsResponse.MonthlyBreakdown m1 = response.getMonthlyBreakdown().get(0);
        assertEquals("Aug 2024", m1.getMonth());
        assertEquals(2, m1.getOrderCount());
        assertEquals(150.0, m1.getTotalPrice());
        assertEquals(75.0, m1.getAvgOrderValue());
        assertEquals(42.86, m1.getPercentage());

        OrderAnalyticsResponse.MonthlyBreakdown m2 = response.getMonthlyBreakdown().get(1);
        assertEquals("Sep 2024", m2.getMonth());
        assertEquals(1, m2.getOrderCount());
        assertEquals(200.0, m2.getTotalPrice());
        assertEquals(200.0, m2.getAvgOrderValue());
        assertEquals(57.14, m2.getPercentage());

        // Verify category breakdown
        assertEquals(2, response.getCategoryBreakdown().size());
        
        // Sorted descending by revenue
        OrderAnalyticsResponse.CategoryBreakdown c1 = response.getCategoryBreakdown().get(0);
        assertEquals("Living Room", c1.getCategory());
        assertEquals(2, c1.getCount());
        assertEquals(300.0, c1.getRevenue());

        OrderAnalyticsResponse.CategoryBreakdown c2 = response.getCategoryBreakdown().get(1);
        assertEquals("Bedroom", c2.getCategory());
        assertEquals(1, c2.getCount());
        assertEquals(50.0, c2.getRevenue());

        // Verify status breakdown
        assertEquals(1, response.getStatusBreakdown().get("delivered"));
        assertEquals(2, response.getStatusBreakdown().get("placed"));
    }
}
