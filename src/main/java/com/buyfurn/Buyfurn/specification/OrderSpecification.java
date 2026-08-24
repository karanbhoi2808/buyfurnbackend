package com.buyfurn.Buyfurn.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.buyfurn.Buyfurn.model.OrderDetails;
import com.buyfurn.Buyfurn.model.Product;
import com.buyfurn.Buyfurn.model.User;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class OrderSpecification {

    public static Specification<OrderDetails> filterOrders(
            String status,
            String searchKey) {

        return (root, query, builder) -> {
            List<Predicate> andPredicates = new ArrayList<>();

            // Status filter (case-insensitive, ignore if "all")
            if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("all")) {
                andPredicates.add(builder.equal(
                        builder.lower(root.get("orderStatus")),
                        status.trim().toLowerCase()
                ));
            }

            // Unified Search filter (OR across multiple fields)
            if (searchKey != null && !searchKey.trim().isEmpty()) {
                String searchLower = searchKey.trim().toLowerCase();
                Long numVal = null;
                try {
                    numVal = Long.parseLong(searchKey.trim());
                } catch (NumberFormatException e) {
                    // Not a number
                }

                List<Predicate> orPredicates = new ArrayList<>();

                // 1. Order ID matching (if search can be parsed as a number)
                if (numVal != null) {
                    orPredicates.add(builder.equal(root.get("orderId"), numVal));
                }

                // 2. Product ID matching (if search can be parsed as a number)
                Join<OrderDetails, Product> productJoin = root.join("product", JoinType.LEFT);
                if (numVal != null) {
                    orPredicates.add(builder.equal(productJoin.get("id"), numVal));
                }
                
                // 3. Product Title matching
                orPredicates.add(builder.like(
                        builder.lower(productJoin.get("title")),
                        "%" + searchLower + "%"
                ));

                // 4. Product Category matching
                orPredicates.add(builder.like(
                        builder.lower(productJoin.get("category")),
                        "%" + searchLower + "%"
                ));

                // 5. Customer Email matching (join User table or check username field)
                Join<OrderDetails, User> userJoin = root.join("user", JoinType.LEFT);
                orPredicates.add(builder.like(
                        builder.lower(userJoin.get("email")),
                        "%" + searchLower + "%"
                ));
                orPredicates.add(builder.like(
                        builder.lower(root.get("username")),
                        "%" + searchLower + "%"
                ));

                // 6. Contact phone number matching
                orPredicates.add(builder.like(
                        builder.lower(root.get("contact")),
                        "%" + searchLower + "%"
                ));

                andPredicates.add(builder.or(orPredicates.toArray(new Predicate[0])));
            }

            return builder.and(andPredicates.toArray(new Predicate[0]));
        };
    }
}
