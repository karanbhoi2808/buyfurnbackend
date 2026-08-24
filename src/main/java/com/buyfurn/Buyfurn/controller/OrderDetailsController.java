package com.buyfurn.Buyfurn.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buyfurn.Buyfurn.model.OrderDetails;
import com.buyfurn.Buyfurn.model.OrderAnalyticsResponse;
import com.buyfurn.Buyfurn.model.OrderListResponse;
import com.buyfurn.Buyfurn.model.AdminDashboardResponse;
import com.buyfurn.Buyfurn.model.OrderInput;
import com.buyfurn.Buyfurn.model.TransactionDetails;
import com.buyfurn.Buyfurn.service.OrderDetailService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class OrderDetailsController {
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("/user/placeOrder/{isSingleProductCheckout}")
    public List<OrderDetails> placeOrder(Principal principal, @RequestBody OrderInput orderInput, @PathVariable boolean isSingleProductCheckout) {
        return orderDetailService.placeOrder(orderInput, principal, isSingleProductCheckout);
    }


	@GetMapping("admin/allOrders")
	public OrderListResponse allOrders(
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "searchKey", required = false) String searchKey,
			@RequestParam(value = "sortBy", defaultValue = "date") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
			@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

		return orderDetailService.getAllOrders(
				status, searchKey, sortBy, sortDir, pageNumber, pageSize
		);
	}

    @GetMapping("admin/orders/analytics")
    public OrderAnalyticsResponse getOrderAnalytics(@RequestParam(value = "status", defaultValue = "all") String status) {
        return orderDetailService.getOrderAnalytics(status);
    }

    @GetMapping("user/myOrders")
    public List<OrderDetails> myOrders(Principal principal) {
        return orderDetailService.myOrders(principal);
    }

    @PutMapping("admin/markAsDelivered/{orderId}")
    public ResponseEntity<Void> markAsDelivered(@PathVariable long orderId) {
        orderDetailService.markAsDelivered(orderId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @GetMapping("user/createTransaction/{amount}")
    public TransactionDetails createTransaction(@PathVariable double amount) {
        return orderDetailService.createTransaction(amount);
    }

}
