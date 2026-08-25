package com.mytoshika.ecommerceoms.service.impl;

import com.mytoshika.ecommerceoms.dto.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        String reviewerName = getLoggedInUserName();
        Product product = getProductById(productId);
        validateReview(productId, keycloakUserId);
        ProductReview productReview = createProductReview(keycloakUserId, reviewerName, product.getId(),request);
        ProductReview savedReview = productReviewRepository.save(productReview);
        return mapToResponse(savedReview);
    }

    @Override
    public PageResponse<ReviewResponse> getAllReview(Long productId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviewPage =  productReviewRepository
                .findByProductId(productId, pageable)
                .map(this::mapToResponse);
        return PageResponse.<ReviewResponse>builder()
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .size(reviewPage.getSize())
                .number(reviewPage.getNumber())
                .content(reviewPage.getContent())
                .build();
    }

    private ProductReview createProductReview(String keycloakUserId, String reviewerName, Long productId, ReviewRequest request) {
        return ProductReview.builder()
                .productId(productId)
                .keycloakUserId(keycloakUserId)
                .reviewerName(reviewerName)
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
        response.setReviewerName(review.getReviewerName());
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
    private String getLoggedInUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaimAsString("name");
    }
}