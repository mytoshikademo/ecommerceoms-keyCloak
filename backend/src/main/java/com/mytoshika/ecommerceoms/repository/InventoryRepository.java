package com.mytoshika.ecommerceoms.repository;

import com.mytoshika.ecommerceoms.entity.Inventory;
import com.mytoshika.ecommerceoms.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct(Product product);

    @Query(value = """
SELECT *
FROM inventory
WHERE product_id = :productId
""", nativeQuery = true)
    Optional<Inventory> findByProductIncludingDeleted(
            @Param("productId") Long productId
    );

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE inventory
        SET is_deleted = false,
            deleted_by = null,
            delete_at = null
        WHERE product_id = :productId
        """, nativeQuery = true)
    void restoreInventory(@Param("productId") Long productId);

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE inventory
    SET is_deleted = true,
        deleted_by = :userId,
        delete_at = CURRENT_DATE
    WHERE product_id = :productId
    """, nativeQuery = true)
    void softDeleteInventory(@Param("productId") Long productId,
                             @Param("userId") Long userId);
}
