package com.farmstock.service;

import com.farmstock.dto.DashboardResponse;
import com.farmstock.repository.CropRepository;
import com.farmstock.repository.HarvestBatchRepository;
import com.farmstock.repository.SaleRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final CropRepository cropRepository;
    private final HarvestBatchRepository harvestBatchRepository;
    private final SaleRepository saleRepository;

    public DashboardService(CropRepository cropRepository, HarvestBatchRepository harvestBatchRepository, SaleRepository saleRepository) {
        this.cropRepository = cropRepository;
        this.harvestBatchRepository = harvestBatchRepository;
        this.saleRepository = saleRepository;
    }

    public DashboardResponse getDashboardSummary() {
        long totalCrops = cropRepository.count();
        long totalHarvestBatches = harvestBatchRepository.count();
        long totalSales = saleRepository.count();
        
        return new DashboardResponse(totalCrops, totalHarvestBatches, totalSales);
    }
}
