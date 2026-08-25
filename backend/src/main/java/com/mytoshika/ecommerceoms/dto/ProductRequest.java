package com.mytoshika.ecommerceoms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jdk.jfr.Description;
import lombok.Builder;
import lombok.Data;

import javax.management.Descriptor;
import java.math.BigDecimal;

@Builder
@Data
public class ProductRequest {

    @Schema(
            description = "Product name",
            example = "Mouse",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Name is required")
    String name;

    @Schema(
            description = "Product Description",
            example = "Latest Wireless Mouse",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Description is required")
    String description;

    @Schema(
            description = "Product Price",
            example = "1200.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Price required")
    @Positive(message = "Price should be positive")
    BigDecimal price;

    @Schema(
            description = "Available Product Quantity",
            example = "100",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "quantity required")
    @PositiveOrZero(message = "Available quantity cannot be negative")
    Integer availableQuantity;

    @NotBlank(message = "Category is required")
    String category;
}
