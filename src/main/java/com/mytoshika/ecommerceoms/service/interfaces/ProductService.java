package com.mytoshika.ecommerceoms.service.interfaces;

import com.mytoshika.ecommerceoms.dto.ProductRequest;
import com.mytoshika.ecommerceoms.dto.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Page<ProductResponse> getAllProducts(int page,
                                         int size,
                                         String sortBy,
                                         String direction,
                                         String keyword,
                                         Double minPrice,
                                         Double maxPrice);

    ProductResponse createProduct(ProductRequest dto);

    ProductResponse updateProduct(Long id, ProductRequest dto);

    ProductResponse getProduct(Long id);

    String deleteProduct(Long id);

    ProductResponse restoreProduct(Long id);

    List<ProductResponse> getDeletedProducts();
}
