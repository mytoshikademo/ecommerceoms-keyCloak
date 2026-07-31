package com.mytoshika.ecommerceoms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SoftDelete(columnName = "is_deleted")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long  id;

    @NotNull(message = "name is required")
    String name;

    @NotNull(message = "name is required")
    String description;

    @Positive(message = "Price should be positive")
    BigDecimal price;

    // isDeleted is implemented directly using soft delete anotation

    LocalDateTime deleteAt;

    Long deletedBy;

    @Column(nullable = false)
    Boolean adminFlag = false;
}
