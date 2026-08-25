package com.mytoshika.ecommerceoms.repository;

import com.mytoshika.ecommerceoms.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductReviewRepository extends MongoRepository<ProductReview, String> {
boolean existsByProductIdAndKeycloakUserId(Long ProductId, String keycloakUserId);
Page<ProductReview> findByProductId(Long productId, Pageable pageable);
}
