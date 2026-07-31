package com.mytoshika.ecommerceoms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

@Getter
@Setter
public class ReviewRequest {

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value=5, message = "Rating must be between 1 to 5")
    private Integer rating;

    @NotNull(message = "Comment is required")
    private String comment;

    private Map<String, Object> metadata;
}
