package com.mytoshika.ecommerceoms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete(columnName = "isDeleted")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name", nullable = false, length = 100)
    private String name;

    @Column(name="email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(unique = true)
    private String keycloakUserId;

    @Column(name="isDisabled", nullable = false)
    private boolean isDisabled;

    @Column(name="deletedAt", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name="deletedBy", nullable = true)
    private Long deletedBy;

    @Column(name="createdAt", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name="updatedAt", nullable = true)
    @UpdateTimestamp
    private LocalDate updatedAt;

}