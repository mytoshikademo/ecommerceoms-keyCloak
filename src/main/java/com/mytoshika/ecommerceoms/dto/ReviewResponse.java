package com.mytoshika.ecommerceoms.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;


@Getter
@Setter
public class ReviewResponse {

    private String id;

    private Long productId;

    private String keycloakUserId;

    private Integer rating;

    private String comment;

    private Map<String, Object> metadata;

    private LocalDateTime createdAt;
}
