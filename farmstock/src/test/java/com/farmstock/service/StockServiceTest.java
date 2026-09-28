package com.farmstock.service;

import com.farmstock.repository.CropRepository;
import com.farmstock.repository.HarvestBatchRepository;
import com.farmstock.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private HarvestBatchRepository harvestBatchRepository;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private CropRepository cropRepository;

    @Mock
    private CropService cropService;

    @InjectMocks
    private StockService stockService;

    @Test
    void testGetCurrentStock() {
        when(harvestBatchRepository.sumQuantityByCropId(1L)).thenReturn(Optional.of(new BigDecimal("100")));
        when(saleRepository.sumQuantityByCropId(1L)).thenReturn(Optional.of(new BigDecimal("30")));

        BigDecimal stock = stockService.getCurrentStock(1L);
        assertEquals(new BigDecimal("70"), stock);
    }
    
    @Test
    void testGetCurrentStock_MultipleSales() {
        when(harvestBatchRepository.sumQuantityByCropId(1L)).thenReturn(Optional.of(new BigDecimal("100")));
        when(saleRepository.sumQuantityByCropId(1L)).thenReturn(Optional.of(new BigDecimal("80")));

        BigDecimal stock = stockService.getCurrentStock(1L);
        assertEquals(new BigDecimal("20"), stock);
    }
}
