package com.mytoshika.ecommerceoms.repository;

import com.mytoshika.ecommerceoms.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByNameIgnoreCase(String name);

    @Query("""
    SELECT p FROM Product p
    WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:minPrice IS NULL OR p.price >= :minPrice)
    AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    AND (:category IS NULL OR LOWER(p.category) = LOWER(:category))
    """)
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("category") String category,
            Pageable pageable
    );

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query(value = """
        SELECT *
        FROM product
        WHERE id = :id
        """, nativeQuery = true)
    Optional<Product> findByIdIncludingDeleted(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE product
        SET is_deleted = false,
            deleted_by = null,
            delete_at = null
        WHERE id = :id
        """, nativeQuery = true)
    void restoreProduct(@Param("id") Long id);

    @Query(value = """
        SELECT *
        FROM product
        WHERE is_deleted = true
        """, nativeQuery = true)
    List<Product> findDeletedProducts();

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE product
    SET is_deleted = true,
        deleted_by = :userId,
        delete_at = CURRENT_DATE
    WHERE id = :id
    """, nativeQuery = true)
    void softDeleteProduct(@Param("id") Long id,
                           @Param("userId") Long userId);

    @Query(value = "SELECT is_deleted FROM product WHERE id = :id", nativeQuery = true)
    boolean getDeletedStatus(@Param("id") Long id);
}

