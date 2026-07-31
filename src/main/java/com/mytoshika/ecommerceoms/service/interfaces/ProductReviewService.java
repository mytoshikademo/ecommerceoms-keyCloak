package com.mytoshika.ecommerceoms.service.interfaces;

import com.mytoshika.ecommerceoms.dto.ReviewRequest;
import com.mytoshika.ecommerceoms.dto.ReviewResponse;

public interface ProductReviewService {

    ReviewResponse createReview(
            Long productId,
            ReviewRequest request
    );
}