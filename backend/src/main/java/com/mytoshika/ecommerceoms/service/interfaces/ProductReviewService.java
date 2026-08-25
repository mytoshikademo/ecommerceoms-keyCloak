package com.mytoshika.ecommerceoms.service.interfaces;

import com.mytoshika.ecommerceoms.dto.PageResponse;
import com.mytoshika.ecommerceoms.dto.ReviewRequest;
import com.mytoshika.ecommerceoms.dto.ReviewResponse;

public interface ProductReviewService {

    ReviewResponse createReview(
            Long productId,
            ReviewRequest request
    );

    PageResponse<ReviewResponse> getAllReview(Long productId, int page, int size);
}