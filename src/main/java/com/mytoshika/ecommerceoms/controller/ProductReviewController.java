package com.mytoshika.ecommerceoms.controller;

import com.mytoshika.ecommerceoms.dto.ReviewRequest;
import com.mytoshika.ecommerceoms.dto.ReviewResponse;
import com.mytoshika.ecommerceoms.service.interfaces.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @PostMapping("/{productId}/reviews")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse response =productReviewService.createReview(productId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}