package com.farmstock.service;

import com.farmstock.dto.SaleRequest;
import com.farmstock.dto.SaleResponse;
import com.farmstock.entity.Crop;
import com.farmstock.entity.Sale;
import com.farmstock.exception.InsufficientStockException;
import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final CropService cropService;
    private final StockService stockService;

    public SaleService(SaleRepository saleRepository, CropService cropService, StockService stockService) {
        this.saleRepository = saleRepository;
        this.cropService = cropService;
        this.stockService = stockService;
    }

    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        Crop crop = cropService.getCropEntityById(request.getCropId());
        
        BigDecimal availableStock = stockService.getCurrentStock(crop.getId());
        
        if (request.getQuantity().compareTo(availableStock) > 0) {
            throw new InsufficientStockException(
                String.format("Insufficient stock. Available stock: %.2f, requested quantity: %.2f.",
                        availableStock, request.getQuantity())
            );
        }
        
        Sale sale = new Sale();
        sale.setCrop(crop);
        sale.setQuantity(request.getQuantity());
        sale.setPricePerUnit(request.getPricePerUnit());
        sale.setSaleDate(request.getSaleDate());
        
        Sale savedSale = saleRepository.save(sale);
        return mapToResponse(savedSale);
    }

    public List<SaleResponse> getAllSales() {
        return saleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale with id " + id + " was not found"));
        return mapToResponse(sale);
    }

    public List<SaleResponse> getSalesByCrop(Long cropId) {
        cropService.getCropEntityById(cropId);
        return saleRepository.findByCropId(cropId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SaleResponse updateSale(Long id, SaleRequest request) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale with id " + id + " was not found"));

        Crop crop = cropService.getCropEntityById(request.getCropId());
        
        BigDecimal availableStock = stockService.getCurrentStock(crop.getId());
        if (sale.getCrop().getId().equals(crop.getId())) {
             availableStock = availableStock.add(sale.getQuantity());
        }
        
        if (request.getQuantity().compareTo(availableStock) > 0) {
            throw new InsufficientStockException(
                String.format("Insufficient stock. Available stock: %.2f, requested quantity: %.2f.",
                        availableStock, request.getQuantity())
            );
        }

        sale.setCrop(crop);
        sale.setQuantity(request.getQuantity());
        sale.setPricePerUnit(request.getPricePerUnit());
        sale.setSaleDate(request.getSaleDate());
        
        Sale updatedSale = saleRepository.save(sale);
        return mapToResponse(updatedSale);
    }

    public void deleteSale(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale with id " + id + " was not found"));
        saleRepository.delete(sale);
    }

    private SaleResponse mapToResponse(Sale sale) {
        SaleResponse response = new SaleResponse();
        response.setId(sale.getId());
        response.setCropId(sale.getCrop().getId());
        response.setCropName(sale.getCrop().getName());
        response.setQuantity(sale.getQuantity());
        response.setPricePerUnit(sale.getPricePerUnit());
        response.setSaleDate(sale.getSaleDate());
        return response;
    }
}
