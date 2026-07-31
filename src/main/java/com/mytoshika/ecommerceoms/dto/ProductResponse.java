package com.mytoshika.ecommerceoms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    Long id;
    String name;
    BigDecimal price;
    Integer availableQuantity;
}
