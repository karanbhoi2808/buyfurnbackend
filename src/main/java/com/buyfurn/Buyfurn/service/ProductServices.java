package com.buyfurn.Buyfurn.service;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.buyfurn.Buyfurn.model.Cart;
import com.buyfurn.Buyfurn.model.Product;
import com.buyfurn.Buyfurn.model.ProductImages;
import com.buyfurn.Buyfurn.model.ProductPageResponse;
import com.buyfurn.Buyfurn.model.User;
import com.buyfurn.Buyfurn.repository.CartRepostitory;
import com.buyfurn.Buyfurn.repository.ProductRepository;
import com.buyfurn.Buyfurn.repository.UserRepository;
import com.buyfurn.Buyfurn.specification.ProductSpecifications;

@Service
public class ProductServices {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartRepostitory cartRepostitory;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SupabaseStorageService supabaseStorageService;

    public Product addProduct(Product product, MultipartFile[] images) throws IOException {
        product.setProductImages(uploadImages(images));
        productRepository.save(product);
        return product;
    }

    public List<ProductImages> uploadImages(MultipartFile[] imgs) throws IOException {

        List<ProductImages> productImages = new ArrayList<ProductImages>();

        for (int i = 0; i < imgs.length; i++) {
            MultipartFile img = imgs[i];
            if (img.isEmpty()) {
                continue;
            }
            String path = supabaseStorageService.uploadFile(img);
            String url = supabaseStorageService.getPublicUrl(path);
            ProductImages images = new ProductImages(img.getOriginalFilename(), img.getContentType(), path, url, i);
            productImages.add(images);
        }

        return productImages;
    }

    public ProductPageResponse getAllProducts(
            int pageNumber,
            int pageSize,
            String searchKey,
            List<String> categories,
            Double minPrice,
            Double maxPrice,
            String stockStatus,
            String sortBy,
            String sortDir) {

        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDir != null && sortDir.trim().equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String cleanSortBy = sortBy.trim().toLowerCase();
            if (cleanSortBy.equals("price")) {
                sort = Sort.by(direction, "price");
            } else if (cleanSortBy.equals("title") || cleanSortBy.equals("name")) {
                sort = Sort.by(direction, "title");
            } else if (cleanSortBy.equals("createddate") || cleanSortBy.equals("latest")) {
                sort = Sort.by(direction, "createdDate");
            } else {
                sort = Sort.by(direction, sortBy);
            }
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdDate");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Specification<Product> spec = ProductSpecifications.filterProducts(searchKey, categories, minPrice, maxPrice, stockStatus);

        Page<Product> paginatedProducts = productRepository.findAll(spec, pageable);

        return new ProductPageResponse(
                paginatedProducts.getContent(),
                paginatedProducts.getNumber(),
                paginatedProducts.getTotalPages(),
                paginatedProducts.getTotalElements(),
                paginatedProducts.getSize()
        );
    }


    public Product getById(Long id) {
        return productRepository.findById(id).get();
    }

    public Product updateProduct(Product product, MultipartFile[] image) throws IOException {

        if (image == null) {
            image = new MultipartFile[0];
        }

        Product newProduct = productRepository.findById(product.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        newProduct.setTitle(product.getTitle());
        newProduct.setColor(product.getColor());
        newProduct.setCareAndMaintenance(product.getCareAndMaintenance());
        newProduct.setWeight(product.getWeight());
        newProduct.setDescription(product.getDescription());
        newProduct.setSeatingCapacity(product.getSeatingCapacity());
        newProduct.setPrice(product.getPrice());
        newProduct.setMaterial(product.getMaterial());
        newProduct.setStockStatus(product.getStockStatus());
        newProduct.setCategory(product.getCategory());
        newProduct.setWarranty(product.getWarranty());

        if (image.length > 0) {
            // Delete old images from Supabase Storage before uploading new ones
            for (ProductImages img : newProduct.getProductImages()) {
                if (img.getPath() != null) {
                    supabaseStorageService.deleteFile(img.getPath());
                }
            }
            newProduct.setProductImages(uploadImages(image));
        }

        productRepository.save(newProduct);
        return newProduct; // Return the updated product
    }

    public String deleteById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            for (ProductImages img : product.get().getProductImages()) {
                if (img.getPath() != null) {
                    supabaseStorageService.deleteFile(img.getPath());
                }
            }
            productRepository.deleteById(id);
            return "Product Deleted !!";
        } else {
            return "Product not found !!";
        }
    }

    public List<Product> getProductDetails(Principal principal, boolean isSingleProductCheckout, long productId) {
        if (isSingleProductCheckout) {
            List<Product> list = new ArrayList<Product>();
            Product product = productRepository.findById(productId).get();
            list.add(product);
            return list;

        } else {

            String username = principal.getName();

            User user = userRepository.findByEmail(username);

            List<Cart> carts = cartRepostitory.findByUser(user);

            List<Product> products = carts.stream().map(x -> x.getProduct()).collect(Collectors.toList());

            return products;
        }
    }


    public List<Product> getLatestProducts() {
        Pageable pageable = PageRequest.of(0, 4); // Page index starts at 0, size = 8
        return productRepository.findTopByOrderByCreatedDateDesc(pageable);
    }
}
