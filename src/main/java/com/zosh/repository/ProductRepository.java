package com.zosh.repository;

import com.zosh.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStoreId(Long storeID);

    //@Query("SELECT p FROM Product p " + "WHERE p.store.id = :storeID AND (" + "LOWER(p.name) LIKE LOWER (CONCAT('%', :query, '%'))" + "LOWER(p.brand) LIKE LOWER (CONCAT('%', :query, '%'))" + "LOWER(p.sku) LIKE LOWER (CONCAT('%', :query, '%'))")
    @Query("SELECT p FROM Product p WHERE p.store.id = :storeID AND (" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%'))" +
            ")")
    List<Product> searchByKeyword(@Param("storeID") Long storeID, @Param("query") String keyword);
}
