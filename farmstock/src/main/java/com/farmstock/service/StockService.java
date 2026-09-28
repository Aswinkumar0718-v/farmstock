package com.farmstock.service;

import com.farmstock.dto.StockResponse;
import com.farmstock.entity.Crop;
import com.farmstock.repository.CropRepository;
import com.farmstock.repository.HarvestBatchRepository;
import com.farmstock.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final HarvestBatchRepository harvestBatchRepository;
    private final SaleRepository saleRepository;
    private final CropRepository cropRepository;
    private final CropService cropService;

    public StockService(HarvestBatchRepository harvestBatchRepository, SaleRepository saleRepository, CropRepository cropRepository, CropService cropService) {
        this.harvestBatchRepository = harvestBatchRepository;
        this.saleRepository = saleRepository;
        this.cropRepository = cropRepository;
        this.cropService = cropService;
    }

    public BigDecimal getCurrentStock(Long cropId) {
        BigDecimal totalHarvested = harvestBatchRepository.sumQuantityByCropId(cropId).orElse(BigDecimal.ZERO);
        BigDecimal totalSold = saleRepository.sumQuantityByCropId(cropId).orElse(BigDecimal.ZERO);
        return totalHarvested.subtract(totalSold);
    }
    
    public StockResponse getStockByCropId(Long cropId) {
        Crop crop = cropService.getCropEntityById(cropId);
        return calculateStockResponse(crop);
    }

    public List<StockResponse> getAllCurrentStock() {
        return cropRepository.findAll().stream()
                .map(this::calculateStockResponse)
                .collect(Collectors.toList());
    }

    private StockResponse calculateStockResponse(Crop crop) {
        BigDecimal totalHarvested = harvestBatchRepository.sumQuantityByCropId(crop.getId()).orElse(BigDecimal.ZERO);
        BigDecimal totalSold = saleRepository.sumQuantityByCropId(crop.getId()).orElse(BigDecimal.ZERO);
        BigDecimal availableStock = totalHarvested.subtract(totalSold);
        return new StockResponse(crop.getId(), crop.getName(), totalHarvested, totalSold, availableStock);
    }
}
