package com.farmstock.service;

import com.farmstock.dto.HarvestBatchRequest;
import com.farmstock.dto.HarvestBatchResponse;
import com.farmstock.entity.Crop;
import com.farmstock.entity.HarvestBatch;
import com.farmstock.repository.HarvestBatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HarvestBatchServiceTest {

    @Mock
    private HarvestBatchRepository harvestBatchRepository;

    @Mock
    private CropService cropService;

    @InjectMocks
    private HarvestBatchService harvestBatchService;

    @Test
    void createHarvestBatch() {
        Crop crop = new Crop();
        crop.setId(1L);
        crop.setName("Tomato");

        when(cropService.getCropEntityById(1L)).thenReturn(crop);

        HarvestBatch saved = new HarvestBatch();
        saved.setId(1L);
        saved.setCrop(crop);
        saved.setQuantity(new BigDecimal("100"));
        saved.setHarvestDate(LocalDate.now());

        when(harvestBatchRepository.save(any(HarvestBatch.class))).thenReturn(saved);

        HarvestBatchRequest req = new HarvestBatchRequest();
        req.setCropId(1L);
        req.setQuantity(new BigDecimal("100"));
        req.setHarvestDate(LocalDate.now());

        HarvestBatchResponse res = harvestBatchService.createHarvestBatch(req);
        assertEquals(1L, res.getId());
        assertEquals("Tomato", res.getCropName());
        assertEquals(new BigDecimal("100"), res.getQuantity());
    }
}
