package com.inditex.ecommerce.priceservice.infrastructure.adapter.out.persistence.repository;

import com.inditex.ecommerce.priceservice.infrastructure.adapter.out.persistence.entity.PriceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataPriceRepository extends JpaRepository<PriceJpaEntity, Long> {

    @Query("""
        SELECT p FROM PriceJpaEntity p
        WHERE p.brandId = :brandId
          AND p.productId = :productId
          AND :applicationDate >= p.startDate
          AND :applicationDate <= p.endDate
        ORDER BY p.priority DESC
    """)
    List<PriceJpaEntity> findAllApplicablePrices(
            @Param("brandId") Long brandId,
            @Param("productId") Long productId,
            @Param("applicationDate") LocalDateTime applicationDate);

    @Query("""
        SELECT p FROM PriceJpaEntity p
        WHERE p.brandId = :brandId
          AND p.productId = :productId
          AND :applicationDate >= p.startDate
          AND :applicationDate <= p.endDate
        ORDER BY p.priority DESC
        FETCH FIRST 1 ROW ONLY
    """)
    Optional<PriceJpaEntity> findFirstApplicablePrice(
            @Param("brandId") Long brandId,
            @Param("productId") Long productId,
            @Param("applicationDate") LocalDateTime applicationDate);
}
