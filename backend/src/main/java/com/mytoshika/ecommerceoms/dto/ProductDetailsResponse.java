package com.mytoshika.ecommerceoms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsResponse {
    Long id;
    String name;
    BigDecimal price;
    String description;
    String category;
    Integer availableQuantity;
}
