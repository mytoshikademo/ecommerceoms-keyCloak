package com.mytoshika.ecommerceoms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {

    @Schema(
            description = "Time when error occurred",
            example = "2026-07-21T23:00:00"
    )
    private LocalDateTime timestamp;

    @Schema(
            description = "HTTP status code",
            example = "400"
    )
    private int status;

    @Schema(
            description = "Short error description",
            example = "Validation error"
    )
    private String error;

    @Schema(
            description = "Detailed error description",
            example = "Name is required"
    )
    private String message;
}