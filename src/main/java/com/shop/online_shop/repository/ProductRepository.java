
package com.shop.online_shop.repository;

import com.shop.online_shop.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    // Tìm kiếm theo tên sản phẩm
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Lọc theo danh mục
    List<Product> findByCategoryId(Long categoryId);

    // Lọc theo khoảng giá
    List<Product> findByPriceBetween(
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    // Tìm kiếm kết hợp nhiều điều kiện
    @Query("""
        SELECT p FROM Product p
        WHERE (:keyword IS NULL OR
               LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:categoryId IS NULL OR p.categoryId = :categoryId)
        AND (:minPrice IS NULL OR p.price >= :minPrice)
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        """)
    List<Product> searchAndFilter(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}
