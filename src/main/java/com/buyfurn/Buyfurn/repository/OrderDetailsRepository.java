package com.buyfurn.Buyfurn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buyfurn.Buyfurn.entity.OrderDetails;
import com.buyfurn.Buyfurn.entity.User;
import com.buyfurn.Buyfurn.projection.OrderAnalyticsProjection;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long>, JpaSpecificationExecutor<OrderDetails> {
    public List<OrderDetails> findByUser(User user);

    List<OrderDetails> findByOrderStatus(String orderStatus);

    @Query("SELECT o.amount AS amount, o.orderStatus AS orderStatus, o.createdAt AS createdAt, p.category AS category " +
           "FROM OrderDetails o LEFT JOIN o.product p")
    List<OrderAnalyticsProjection> findAllAnalytics();

    @Query("SELECT o.amount AS amount, o.orderStatus AS orderStatus, o.createdAt AS createdAt, p.category AS category " +
           "FROM OrderDetails o LEFT JOIN o.product p " +
           "WHERE LOWER(o.orderStatus) = LOWER(:orderStatus)")
    List<OrderAnalyticsProjection> findAnalyticsByOrderStatus(@Param("orderStatus") String orderStatus);
}
