package com.buyfurn.Buyfurn.specification;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import com.buyfurn.Buyfurn.model.Product;
import jakarta.persistence.criteria.Predicate;

public class ProductSpecifications {

    public static Specification<Product> filterProducts(
            String searchKey,
            List<String> categories,
            Double minPrice,
            Double maxPrice,
            String stockStatus) {

        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Title filter (case-insensitive contains)
            if (searchKey != null && !searchKey.trim().isEmpty()) {
                predicates.add(builder.like(
                        builder.lower(root.get("title")),
                        "%" + searchKey.trim().toLowerCase() + "%"
                ));
            }

            // Categories filter (multi-select)
            if (categories != null && !categories.isEmpty()) {
                List<String> cleanCategories = categories.stream()
                        .filter(c -> c != null && !c.trim().isEmpty())
                        .map(String::trim)
                        .toList();
                if (!cleanCategories.isEmpty()) {
                    predicates.add(root.get("category").in(cleanCategories));
                }
            }

            // Price range filter
            if (minPrice != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            // Stock status filter
            if (stockStatus != null && !stockStatus.trim().isEmpty()) {
                String status = stockStatus.trim().toLowerCase();
                if (status.equals("in_stock") || status.equals("instock") || status.equals("in stock")) {
                    predicates.add(builder.or(
                        builder.equal(builder.lower(root.get("stockStatus")), "in stock"),
                        builder.equal(builder.lower(root.get("stockStatus")), "in_stock"),
                        builder.equal(builder.lower(root.get("stockStatus")), "instock")
                    ));
                } else if (status.equals("out_of_stock") || status.equals("outofstock") || status.equals("out of stock")) {
                    predicates.add(builder.or(
                        builder.equal(builder.lower(root.get("stockStatus")), "out of stock"),
                        builder.equal(builder.lower(root.get("stockStatus")), "out_of_stock"),
                        builder.equal(builder.lower(root.get("stockStatus")), "outofstock")
                    ));
                } else {
                    predicates.add(builder.equal(builder.lower(root.get("stockStatus")), status));
                }
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
