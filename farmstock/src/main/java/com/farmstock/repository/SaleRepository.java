package com.farmstock.repository;

import com.farmstock.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByCropId(Long cropId);

    @Query("SELECT SUM(s.quantity) FROM Sale s WHERE s.crop.id = :cropId")
    Optional<BigDecimal> sumQuantityByCropId(@Param("cropId") Long cropId);

    @Query("SELECT SUM(s.quantity * s.pricePerUnit) FROM Sale s WHERE s.crop.id = :cropId AND s.saleDate >= :fromDate AND s.saleDate <= :toDate")
    Optional<BigDecimal> calculateRevenueByCropIdAndDateRange(@Param("cropId") Long cropId, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}
