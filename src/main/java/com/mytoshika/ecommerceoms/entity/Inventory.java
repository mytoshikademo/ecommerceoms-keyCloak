package com.mytoshika.ecommerceoms.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SoftDelete(columnName = "is_deleted")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @PositiveOrZero(message = "Available quantity cannot be negative")
    private Integer availableQuantity;

    @PositiveOrZero(message = "Reserved quantity cannot be negative")
    private Integer reservedQuantity;

    LocalDate deleteAt;

    Long deletedBy;
}
