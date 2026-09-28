package com.farmstock.service;

import com.farmstock.dto.SaleRequest;
import com.farmstock.dto.SaleResponse;
import com.farmstock.entity.Crop;
import com.farmstock.entity.Sale;
import com.farmstock.exception.InsufficientStockException;
import com.farmstock.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private CropService cropService;

    @Mock
    private StockService stockService;

    @InjectMocks
    private SaleService saleService;

    @Test
    void shouldCreateSaleWhenStockIsSufficient() {
        Crop crop = new Crop();
        crop.setId(1L);
        crop.setName("Tomato");

        when(cropService.getCropEntityById(1L)).thenReturn(crop);
        when(stockService.getCurrentStock(1L)).thenReturn(new BigDecimal("100"));

        Sale saved = new Sale();
        saved.setId(1L);
        saved.setCrop(crop);
        saved.setQuantity(new BigDecimal("30"));
        saved.setPricePerUnit(new BigDecimal("50"));
        saved.setSaleDate(LocalDate.now());

        when(saleRepository.save(any(Sale.class))).thenReturn(saved);

        SaleRequest req = new SaleRequest();
        req.setCropId(1L);
        req.setQuantity(new BigDecimal("30"));
        req.setPricePerUnit(new BigDecimal("50"));
        req.setSaleDate(LocalDate.now());

        SaleResponse res = saleService.createSale(req);
        assertEquals(new BigDecimal("30"), res.getQuantity());
        verify(saleRepository, times(1)).save(any(Sale.class));
    }

    @Test
    void shouldRejectSaleWhenQuantityExceedsAvailableStock() {
        Crop crop = new Crop();
        crop.setId(1L);
        crop.setName("Tomato");

        when(cropService.getCropEntityById(1L)).thenReturn(crop);
        when(stockService.getCurrentStock(1L)).thenReturn(new BigDecimal("50")); // Adjusted to match the new strict requirement example

        SaleRequest req = new SaleRequest();
        req.setCropId(1L);
        req.setQuantity(new BigDecimal("60")); // Adjusted to match the new strict requirement example
        req.setPricePerUnit(new BigDecimal("50"));
        req.setSaleDate(LocalDate.now());

        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> saleService.createSale(req));

        assertEquals("Insufficient stock. Available stock: 50.00, requested quantity: 60.00.", exception.getMessage());
        verify(saleRepository, never()).save(any(Sale.class));
    }
}
