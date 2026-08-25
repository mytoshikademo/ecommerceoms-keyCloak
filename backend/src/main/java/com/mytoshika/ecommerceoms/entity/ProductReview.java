package com.mytoshika.ecommerceoms.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter
@Setter
@Document(collection = "product_reviews")
public class ProductReview {
    @Id
    private String id;

    private Long productId;

    private String keycloakUserId;

    private String reviewerName;

    private Integer rating;

    private String comment;

    private Map<String, Object> metadata;

    private LocalDateTime createdAt;
}
