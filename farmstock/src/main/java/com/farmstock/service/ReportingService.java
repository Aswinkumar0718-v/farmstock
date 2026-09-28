package com.farmstock.service;

import com.farmstock.dto.RevenueResponse;
import com.farmstock.entity.Crop;
import com.farmstock.exception.InvalidDataException;
import com.farmstock.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ReportingService {

    private final SaleRepository saleRepository;
    private final CropService cropService;

    public ReportingService(SaleRepository saleRepository, CropService cropService) {
        this.saleRepository = saleRepository;
        this.cropService = cropService;
    }

    public RevenueResponse getRevenueByCropAndDateRange(Long cropId, LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new InvalidDataException("Start date cannot be after end date.");
        }

        Crop crop = cropService.getCropEntityById(cropId);
        
        BigDecimal totalRevenue = saleRepository.calculateRevenueByCropIdAndDateRange(cropId, from, to)
                .orElse(BigDecimal.ZERO);
                
        return new RevenueResponse(crop.getId(), crop.getName(), from, to, totalRevenue);
    }
}
