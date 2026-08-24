package com.buyfurn.Buyfurn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buyfurn.Buyfurn.model.Cart;
import com.buyfurn.Buyfurn.model.OrderDetails;
import com.buyfurn.Buyfurn.model.User;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long>, JpaSpecificationExecutor<OrderDetails> {
	public List<OrderDetails> findByUser(User user);

    List<OrderDetails> findByOrderStatus(String orderStatus);



    @Query("SELECT o.amount AS amount, o.orderStatus AS orderStatus, o.createdDate AS createdDate, p.category AS category " +
           "FROM OrderDetails o LEFT JOIN o.product p")
    List<OrderAnalyticsProjection> findAllAnalytics();

    @Query("SELECT o.amount AS amount, o.orderStatus AS orderStatus, o.createdDate AS createdDate, p.category AS category " +
           "FROM OrderDetails o LEFT JOIN o.product p " +
           "WHERE LOWER(o.orderStatus) = LOWER(:orderStatus)")
    List<OrderAnalyticsProjection> findAnalyticsByOrderStatus(@Param("orderStatus") String orderStatus);

}
