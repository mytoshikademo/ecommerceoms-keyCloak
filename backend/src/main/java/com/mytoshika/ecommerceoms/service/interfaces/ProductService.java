package com.mytoshika.ecommerceoms.service.interfaces;

import com.mytoshika.ecommerceoms.dto.ProductDetailsResponse;
import com.mytoshika.ecommerceoms.dto.PageResponse;
import com.mytoshika.ecommerceoms.dto.ProductRequest;
import com.mytoshika.ecommerceoms.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    PageResponse<ProductResponse> getAllProducts(int page,
                                int size,
                                String sortBy,
                                String direction,
                                String keyword,
                                Double minPrice,
                                Double maxPrice,
                                String category);

    ProductResponse createProduct(ProductRequest dto);

    ProductResponse updateProduct(Long id, ProductRequest dto);

    ProductDetailsResponse getProduct(Long id);

    String deleteProduct(Long id);

    ProductResponse restoreProduct(Long id);

    List<ProductResponse> getDeletedProducts();
}
