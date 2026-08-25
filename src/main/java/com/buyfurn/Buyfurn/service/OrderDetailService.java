package com.buyfurn.Buyfurn.service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.buyfurn.Buyfurn.dto.DtoMapper;
import com.buyfurn.Buyfurn.dto.OrderAnalyticsResponse;
import com.buyfurn.Buyfurn.dto.OrderDetailsDto;
import com.buyfurn.Buyfurn.dto.OrderInputDto;
import com.buyfurn.Buyfurn.dto.OrderListResponse;
import com.buyfurn.Buyfurn.dto.OrderQuantityDto;
import com.buyfurn.Buyfurn.dto.TransactionDetails;
import com.buyfurn.Buyfurn.entity.Cart;
import com.buyfurn.Buyfurn.entity.OrderDetails;
import com.buyfurn.Buyfurn.entity.Product;
import com.buyfurn.Buyfurn.entity.User;
import com.buyfurn.Buyfurn.exception.ResourceNotFoundException;
import com.buyfurn.Buyfurn.projection.OrderAnalyticsProjection;
import com.buyfurn.Buyfurn.projection.OrderDetailsProjection;
import com.buyfurn.Buyfurn.repository.CartRepository;
import com.buyfurn.Buyfurn.repository.OrderDetailsRepository;
import com.buyfurn.Buyfurn.repository.ProductRepository;
import com.buyfurn.Buyfurn.repository.UserRepository;
import com.buyfurn.Buyfurn.specification.OrderSpecification;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class OrderDetailService {

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    @Value("${razorpay.currency:INR}")
    private String razorpayCurrency;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;
    
    private final static String ORDER_PLACED = "Placed";
    private final static String ORDER_DELIVERED = "Delivered";

    public List<OrderDetailsDto> placeOrder(OrderInputDto orderInput, Principal principal, boolean isSingleProductCheckout) {
        List<OrderQuantityDto> orderQuantities = orderInput.getOrderQuantities();

        if (orderQuantities == null || orderQuantities.isEmpty()) {
            return List.of();
        }

        String username = principal.getName();
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        
        List<OrderDetails> placedOrders = new ArrayList<>();
        for (OrderQuantityDto o : orderQuantities) {
            Product product = productRepository.findById(o.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + o.getProductId()));
            
            OrderDetails orderDetail = new OrderDetails(
                    user.getEmail(),
                    DtoMapper.toEntity(orderInput.getAddress()),
                    orderInput.getContactNumber(),
                    ORDER_PLACED,
                    product.getPrice() * o.getQuantity(),
                    product, 
                    user,
                    orderInput.getTransactionId());

            orderDetailsRepository.save(orderDetail);
            placedOrders.add(orderDetail);

            if (!isSingleProductCheckout) {
                List<Cart> carts = cartRepository.findByUser(user);
                carts.forEach(x -> cartRepository.delete(x));
            }
        }
        
        return placedOrders.stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public OrderListResponse getAllOrders(
            String status,
            String searchKey,
            String sortBy,
            String sortDir,
            int pageNumber,
            int pageSize) {

        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDir != null && sortDir.trim().equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String cleanSortBy = sortBy.trim().toLowerCase();
            if (cleanSortBy.equals("date") || cleanSortBy.equals("createddate")) {
                sort = Sort.by(direction, "createdAt");
            } else if (cleanSortBy.equals("amount") || cleanSortBy.equals("price")) {
                sort = Sort.by(direction, "amount");
            } else if (cleanSortBy.equals("status") || cleanSortBy.equals("orderstatus")) {
                sort = Sort.by(direction, "orderStatus");
            } else {
                sort = Sort.by(direction, sortBy);
            }
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Specification<OrderDetails> spec = OrderSpecification.filterOrders(status, searchKey);

        List<OrderAnalyticsProjection> globalOrders = orderDetailsRepository.findAllAnalytics();

        long totalOrders = globalOrders.size();
        long placedCount = 0;
        long deliveredCount = 0;
        double totalRevenue = 0;

        for (OrderAnalyticsProjection o : globalOrders) {
            Double amt = o.getAmount();
            if (amt != null) {
                totalRevenue += amt;
            }
            if (o.getOrderStatus() != null) {
                if (o.getOrderStatus().equalsIgnoreCase("Placed")) {
                    placedCount++;
                } else if (o.getOrderStatus().equalsIgnoreCase("Delivered")) {
                    deliveredCount++;
                }
            }
        }

        Page<OrderDetailsProjection> paginatedResult = orderDetailsRepository.findBy(spec, q -> q.as(OrderDetailsProjection.class).page(pageable));

        List<OrderListResponse.OrderResponseDTO> ordersDTOList = new java.util.ArrayList<>();

        for (OrderDetailsProjection o : paginatedResult.getContent()) {
            OrderListResponse.UserResponseDTO userDTO = null;
            if (o.getUser() != null) {
                String name = o.getUser().getName();
                String emailVal = o.getUser().getEmail();
                String usernameVal = "";
                if (emailVal != null) {
                    usernameVal = emailVal.split("@")[0];
                }
                userDTO = new OrderListResponse.UserResponseDTO(name, usernameVal, emailVal);
            } else {
                String emailVal = o.getUsername();
                String usernameVal = "";
                if (emailVal != null) {
                    usernameVal = emailVal.split("@")[0];
                }
                userDTO = new OrderListResponse.UserResponseDTO("", usernameVal, emailVal);
            }

            OrderListResponse.AddressResponseDTO addrDTO = null;
            if (o.getAddress() != null) {
                addrDTO = new OrderListResponse.AddressResponseDTO(
                        o.getAddress().getAddress(),
                        o.getAddress().getCity(),
                        o.getAddress().getState(),
                        o.getAddress().getPincode()
                );
            }

            OrderListResponse.ProductResponseDTO prodDTO = null;
            if (o.getProduct() != null) {
                List<OrderListResponse.ProductImageResponseDTO> imgDTOList = new java.util.ArrayList<>();
                if (o.getProduct().getProductImages() != null && !o.getProduct().getProductImages().isEmpty()) {
                    OrderDetailsProjection.ProductImageProj firstImg = o.getProduct().getProductImages().get(0);
                    imgDTOList.add(new OrderListResponse.ProductImageResponseDTO(firstImg.getName(), firstImg.getUrl()));
                }
                prodDTO = new OrderListResponse.ProductResponseDTO(
                        o.getProduct().getId(),
                        o.getProduct().getTitle(),
                        o.getProduct().getPrice(),
                        o.getProduct().getCategory(),
                        imgDTOList
                );
            }

            ordersDTOList.add(new OrderListResponse.OrderResponseDTO(
                    o.getOrderId(),
                    o.getOrderStatus(),
                    o.getCreatedAt(),
                    o.getContact(),
                    userDTO,
                    addrDTO,
                    prodDTO
            ));
        }

        double roundedTotalRevenue = Math.round(totalRevenue * 100.0) / 100.0;

        return new OrderListResponse(
                totalOrders,
                placedCount,
                deliveredCount,
                roundedTotalRevenue,
                paginatedResult.getTotalPages(),
                paginatedResult.getNumber(),
                ordersDTOList
        );
    }

    public OrderAnalyticsResponse getOrderAnalytics(String status) {
        List<OrderAnalyticsProjection> orders;
        if (status == null || status.equalsIgnoreCase("all")) {
            orders = orderDetailsRepository.findAllAnalytics();
        } else {
            orders = orderDetailsRepository.findAnalyticsByOrderStatus(status);
        }

        double totalRevenue = 0;
        int totalOrders = orders.size();
        int deliveredCount = 0;
        int placedCount = 0;

        for (OrderAnalyticsProjection order : orders) {
            Double amt = order.getAmount();
            if (amt != null) {
                totalRevenue += amt;
            }
            String s = order.getOrderStatus();
            if (s != null) {
                if (s.equalsIgnoreCase("delivered")) {
                    deliveredCount++;
                } else if (s.equalsIgnoreCase("placed")) {
                    placedCount++;
                }
            }
        }

        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;

        double roundedTotalRevenue = Math.round(totalRevenue * 100.0) / 100.0;
        double roundedAverageOrderValue = Math.round(averageOrderValue * 100.0) / 100.0;

        Map<YearMonth, List<OrderAnalyticsProjection>> ordersByMonth = new TreeMap<>();
        for (OrderAnalyticsProjection order : orders) {
            LocalDateTime createdDate = order.getCreatedAt();
            if (createdDate == null) {
                createdDate = LocalDateTime.now();
            }
            YearMonth ym = YearMonth.from(createdDate);
            ordersByMonth.computeIfAbsent(ym, k -> new ArrayList<>()).add(order);
        }

        List<OrderAnalyticsResponse.MonthlyBreakdown> monthlyBreakdownList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);
        double peakRevenue = 0;
        String peakMonthName = "";

        for (Map.Entry<YearMonth, List<OrderAnalyticsProjection>> entry : ordersByMonth.entrySet()) {
            YearMonth ym = entry.getKey();
            List<OrderAnalyticsProjection> monthOrders = entry.getValue();

            String monthStr = ym.atDay(1).format(formatter);
            int mOrderCount = monthOrders.size();
            double mTotalPrice = 0;
            for (OrderAnalyticsProjection o : monthOrders) {
                Double amt = o.getAmount();
                if (amt != null) {
                    mTotalPrice += amt;
                }
            }

            double mAvgOrderValue = mOrderCount > 0 ? mTotalPrice / mOrderCount : 0.0;
            double mPercentage = totalRevenue > 0 ? (mTotalPrice / totalRevenue) * 100.0 : 0.0;

            double roundedMTotalPrice = Math.round(mTotalPrice * 100.0) / 100.0;
            double roundedMAvgOrderValue = Math.round(mAvgOrderValue * 100.0) / 100.0;
            double roundedMPercentage = Math.round(mPercentage * 100.0) / 100.0;

            monthlyBreakdownList.add(new OrderAnalyticsResponse.MonthlyBreakdown(
                    monthStr, mOrderCount, roundedMTotalPrice, roundedMAvgOrderValue, roundedMPercentage
            ));

            if (roundedMTotalPrice > peakRevenue) {
                peakRevenue = roundedMTotalPrice;
                peakMonthName = monthStr;
            }
        }

        OrderAnalyticsResponse.PeakMonth peakMonthObj;
        if (totalOrders > 0 && !peakMonthName.isEmpty()) {
            peakMonthObj = new OrderAnalyticsResponse.PeakMonth(peakMonthName, peakRevenue);
        } else {
            peakMonthObj = new OrderAnalyticsResponse.PeakMonth("", 0.0);
        }

        OrderAnalyticsResponse.Summary summary = new OrderAnalyticsResponse.Summary(
                roundedTotalRevenue, totalOrders, roundedAverageOrderValue, deliveredCount, placedCount, peakMonthObj
        );

        Map<String, List<OrderAnalyticsProjection>> ordersByCategory = new HashMap<>();
        for (OrderAnalyticsProjection order : orders) {
            String category = order.getCategory();
            if (category == null || category.isEmpty()) {
                category = "Unknown";
            }
            ordersByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(order);
        }

        List<OrderAnalyticsResponse.CategoryBreakdown> categoryBreakdownList = new ArrayList<>();
        for (Map.Entry<String, List<OrderAnalyticsProjection>> entry : ordersByCategory.entrySet()) {
            String category = entry.getKey();
            List<OrderAnalyticsProjection> catOrders = entry.getValue();

            int catCount = catOrders.size();
            double catRevenue = 0;
            for (OrderAnalyticsProjection o : catOrders) {
                Double amt = o.getAmount();
                if (amt != null) {
                    catRevenue += amt;
                }
            }

            double roundedCatRevenue = Math.round(catRevenue * 100.0) / 100.0;
            categoryBreakdownList.add(new OrderAnalyticsResponse.CategoryBreakdown(category, catCount, roundedCatRevenue));
        }

        categoryBreakdownList.sort((a, b) -> Double.compare(b.getRevenue(), a.getRevenue()));

        Map<String, Integer> statusBreakdownMap = new LinkedHashMap<>();
        statusBreakdownMap.put("delivered", deliveredCount);
        statusBreakdownMap.put("placed", placedCount);

        for (OrderAnalyticsProjection order : orders) {
            String statusStr = order.getOrderStatus();
            if (statusStr != null) {
                String key = statusStr.toLowerCase();
                if (!key.equals("delivered") && !key.equals("placed")) {
                    statusBreakdownMap.put(key, statusBreakdownMap.getOrDefault(key, 0) + 1);
                }
            }
        }

        return new OrderAnalyticsResponse(summary, monthlyBreakdownList, categoryBreakdownList, statusBreakdownMap);
    }

    public OrderDetailsDto markAsDelivered(long orderId) {
        OrderDetails orderDetails = orderDetailsRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        orderDetails.setOrderStatus(ORDER_DELIVERED);
        orderDetailsRepository.save(orderDetails);
        return DtoMapper.toDto(orderDetails);
    }

    public List<OrderDetailsDto> myOrders(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return orderDetailsRepository.findByUser(user).stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public TransactionDetails createTransaction(double amount) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("amount", (amount * 100));
            jsonObject.put("currency", razorpayCurrency);

            RazorpayClient razorpayClient = new RazorpayClient(razorpayKey, razorpaySecret);
            Order order = razorpayClient.orders.create(jsonObject);
            return prepareTransactionDetails(order);
        } catch (Exception e) {
            System.err.println("Razorpay Transaction generation failed: " + e.getMessage());
        }
        return null;
    }

    private TransactionDetails prepareTransactionDetails(Order order) {
        String orderId = order.get("id");
        String currency = order.get("currency");
        Integer amount = order.get("amount");

        return new TransactionDetails(orderId, currency, amount, razorpayKey);
    }
}
