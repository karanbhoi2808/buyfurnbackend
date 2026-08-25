package com.buyfurn.Buyfurn.service;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.buyfurn.Buyfurn.dto.DtoMapper;
import com.buyfurn.Buyfurn.dto.ProductDto;
import com.buyfurn.Buyfurn.dto.ProductPageResponse;
import com.buyfurn.Buyfurn.dto.ProductRequestDto;
import com.buyfurn.Buyfurn.entity.Cart;
import com.buyfurn.Buyfurn.entity.Product;
import com.buyfurn.Buyfurn.entity.ProductImage;
import com.buyfurn.Buyfurn.entity.User;
import com.buyfurn.Buyfurn.exception.ResourceNotFoundException;
import com.buyfurn.Buyfurn.repository.CartRepository;
import com.buyfurn.Buyfurn.repository.ProductRepository;
import com.buyfurn.Buyfurn.repository.UserRepository;
import com.buyfurn.Buyfurn.specification.ProductSpecifications;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SupabaseStorageService supabaseStorageService;

    public ProductDto addProduct(ProductRequestDto productDto, MultipartFile[] images) throws IOException {
        Product product = Product.builder()
                .title(productDto.getTitle())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .warranty(productDto.getWarranty())
                .category(productDto.getCategory())
                .color(productDto.getColor())
                .material(productDto.getMaterial())
                .seatingCapacity(productDto.getSeatingCapacity())
                .weight(productDto.getWeight())
                .careAndMaintenance(productDto.getCareAndMaintenance())
                .stockStatus(productDto.getStockStatus())
                .productImages(uploadImages(images))
                .build();
                
        productRepository.save(product);
        return DtoMapper.toDto(product);
    }

    public List<ProductImage> uploadImages(MultipartFile[] imgs) throws IOException {
        List<ProductImage> productImages = new ArrayList<>();
        if (imgs == null) {
            return productImages;
        }
        for (int i = 0; i < imgs.length; i++) {
            MultipartFile img = imgs[i];
            if (img.isEmpty()) {
                continue;
            }
            String path = supabaseStorageService.uploadFile(img);
            String url = supabaseStorageService.getPublicUrl(path);
            ProductImage image = new ProductImage(null, img.getOriginalFilename(), img.getContentType(), path, url, i, null, null);
            productImages.add(image);
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
                sort = Sort.by(direction, "createdAt");
            } else {
                sort = Sort.by(direction, sortBy);
            }
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Specification<Product> spec = ProductSpecifications.filterProducts(searchKey, categories, minPrice, maxPrice, stockStatus);
        
        Page<Product> paginatedProducts = productRepository.findAll(spec, pageable);

        List<ProductDto> dtoList = paginatedProducts.getContent().stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());

        return new ProductPageResponse(
                dtoList,
                paginatedProducts.getNumber(),
                paginatedProducts.getTotalPages(),
                paginatedProducts.getTotalElements(),
                paginatedProducts.getSize()
        );
    }

    public ProductDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        return DtoMapper.toDto(product);
    }

    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    public ProductDto updateProduct(ProductRequestDto productDto, MultipartFile[] image) throws IOException {
        if (image == null) {
            image = new MultipartFile[0];
        }

        Product newProduct = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productDto.getId()));
                
        newProduct.setTitle(productDto.getTitle());
        newProduct.setColor(productDto.getColor());
        newProduct.setCareAndMaintenance(productDto.getCareAndMaintenance());
        newProduct.setWeight(productDto.getWeight());
        newProduct.setDescription(productDto.getDescription());
        newProduct.setSeatingCapacity(productDto.getSeatingCapacity());
        newProduct.setPrice(productDto.getPrice());
        newProduct.setMaterial(productDto.getMaterial());
        newProduct.setStockStatus(productDto.getStockStatus());
        newProduct.setCategory(productDto.getCategory());
        newProduct.setWarranty(productDto.getWarranty());

        if (image.length > 0) {
            // Delete old images from Supabase Storage before uploading new ones
            for (ProductImage img : newProduct.getProductImages()) {
                if (img.getPath() != null) {
                    supabaseStorageService.deleteFile(img.getPath());
                }
            }
            newProduct.setProductImages(uploadImages(image));
        }

        productRepository.save(newProduct);
        return DtoMapper.toDto(newProduct);
    }

    public String deleteById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            for (ProductImage img : product.get().getProductImages()) {
                if (img.getPath() != null) {
                    supabaseStorageService.deleteFile(img.getPath());
                }
            }
            productRepository.deleteById(id);
            return "Product Deleted !!";
        } else {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
    }

    public List<ProductDto> getProductDetails(Principal principal, boolean isSingleProductCheckout, long productId) {
        if (isSingleProductCheckout) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
            return List.of(DtoMapper.toDto(product));
        } else {
            String username = principal.getName();
            User user = userRepository.findByEmail(username);
            if (user == null) {
                throw new ResourceNotFoundException("User not found");
            }
            List<Cart> carts = cartRepository.findByUser(user);
            return carts.stream()
                    .map(Cart::getProduct)
                    .map(DtoMapper::toDto)
                    .collect(Collectors.toList());
        }
    }

    public List<ProductDto> getLatestProducts() {
        Pageable pageable = PageRequest.of(0, 4);
        return productRepository.findTopByOrderByCreatedAtDesc(pageable).stream()
                .map(DtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
