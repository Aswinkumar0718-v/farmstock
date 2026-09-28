package com.farmstock.service;

import com.farmstock.dto.RevenueResponse;
import com.farmstock.entity.Crop;
import com.farmstock.exception.InvalidDataException;
import com.farmstock.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportingServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private CropService cropService;

    @InjectMocks
    private ReportingService reportingService;

    @Test
    void testGetRevenue() {
        Crop crop = new Crop();
        crop.setId(1L);
        crop.setName("Tomato");

        when(cropService.getCropEntityById(1L)).thenReturn(crop);

        LocalDate from = LocalDate.of(2026, 9, 1);
        LocalDate to = LocalDate.of(2026, 9, 30);

        when(saleRepository.calculateRevenueByCropIdAndDateRange(1L, from, to))
                .thenReturn(Optional.of(new BigDecimal("1000")));

        RevenueResponse res = reportingService.getRevenueByCropAndDateRange(1L, from, to);
        assertEquals(new BigDecimal("1000"), res.getTotalRevenue());
    }

    @Test
    void testInvalidDateRange() {
        LocalDate from = LocalDate.of(2026, 9, 30);
        LocalDate to = LocalDate.of(2026, 9, 1);

        assertThrows(InvalidDataException.class, () -> reportingService.getRevenueByCropAndDateRange(1L, from, to));
    }
}
