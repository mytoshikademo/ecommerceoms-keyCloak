package com.mytoshika.ecommerceoms.controller;

import com.mytoshika.ecommerceoms.dto.PageResponse;
import com.mytoshika.ecommerceoms.dto.ReviewRequest;
import com.mytoshika.ecommerceoms.dto.ReviewResponse;
import com.mytoshika.ecommerceoms.service.interfaces.ProductReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviews(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        PageResponse<ReviewResponse> response = productReviewService.getAllReview(productId,page,size);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}