package com.mytoshika.ecommerceoms.service.impl;

import com.mytoshika.ecommerceoms.dto.ReviewRequest;
import com.mytoshika.ecommerceoms.dto.ReviewResponse;
import com.mytoshika.ecommerceoms.entity.Product;
import com.mytoshika.ecommerceoms.entity.ProductReview;
import com.mytoshika.ecommerceoms.exception.DuplicateReviewException;
import com.mytoshika.ecommerceoms.exception.ResourceNotFoundException;
import com.mytoshika.ecommerceoms.repository.ProductRepository;
import com.mytoshika.ecommerceoms.repository.ProductReviewRepository;
import com.mytoshika.ecommerceoms.service.interfaces.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductReviewServiceImpl implements ProductReviewService {
    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;

    @Override
    public ReviewResponse createReview(Long productId,ReviewRequest request) {
        String keycloakUserId = getLoggedInUserKeycloakId();
        Product product = getProductById(productId);
        validateReview(productId, keycloakUserId);
        ProductReview productReview = createProductReview(keycloakUserId, product.getId(),request);
        ProductReview savedReview = productReviewRepository.save(productReview);
        return mapToResponse(savedReview);
    }

    private ProductReview createProductReview(String keycloakUserId, Long productId, ReviewRequest request) {
        return ProductReview.builder()
                .productId(productId)
                .keycloakUserId(keycloakUserId)
                .rating(request.getRating())
                .comment(request.getComment())
                .metadata(request.getMetadata())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private ReviewResponse mapToResponse(ProductReview review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setProductId(review.getProductId());
        response.setKeycloakUserId(review.getKeycloakUserId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setMetadata(review.getMetadata());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }

    private Product getProductById( Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() ->
        new ResourceNotFoundException("Product not found"));
    }

    private void validateReview(Long productId, String keycloakUserId){
        boolean alreadyReviewed =
                productReviewRepository.existsByProductIdAndKeycloakUserId(
                        productId,
                        keycloakUserId
                );
        if (alreadyReviewed) {
            throw new DuplicateReviewException(
                    "You have already reviewed this product"
            );
        }
    }
    private String getLoggedInUserKeycloakId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }
}