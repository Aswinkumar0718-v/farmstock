package com.farmstock.repository;

import com.farmstock.entity.HarvestBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HarvestBatchRepository extends JpaRepository<HarvestBatch, Long> {
    List<HarvestBatch> findByCropId(Long cropId);

    @Query("SELECT SUM(h.quantity) FROM HarvestBatch h WHERE h.crop.id = :cropId")
    Optional<BigDecimal> sumQuantityByCropId(@Param("cropId") Long cropId);
}
