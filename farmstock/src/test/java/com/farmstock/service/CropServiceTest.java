package com.farmstock.service;

import com.farmstock.dto.CropRequest;
import com.farmstock.dto.CropResponse;
import com.farmstock.entity.Crop;
import com.farmstock.entity.HarvestBatch;
import com.farmstock.entity.Sale;
import com.farmstock.exception.InvalidDataException;
import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.repository.CropRepository;
import com.farmstock.repository.HarvestBatchRepository;
import com.farmstock.repository.SaleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CropServiceTest {

    @Mock
    private CropRepository cropRepository;
    
    @Mock
    private HarvestBatchRepository harvestBatchRepository;
    
    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private CropService cropService;

    @Test
    void createCrop() {
        CropRequest req = new CropRequest();
        req.setName("Tomato");
        req.setCategory("Veg");

        Crop saved = new Crop();
        saved.setId(1L);
        saved.setName("Tomato");
        saved.setCategory("Veg");

        when(cropRepository.save(any(Crop.class))).thenReturn(saved);

        CropResponse res = cropService.createCrop(req);
        assertEquals(1L, res.getId());
        assertEquals("Tomato", res.getName());
    }

    @Test
    void getCropById_Found() {
        Crop crop = new Crop();
        crop.setId(1L);
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));

        CropResponse res = cropService.getCropById(1L);
        assertEquals(1L, res.getId());
    }

    @Test
    void getCropById_NotFound() {
        when(cropRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cropService.getCropById(1L));
    }

    @Test
    void deleteCrop_Successful() {
        Crop crop = new Crop();
        crop.setId(1L);
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));
        when(harvestBatchRepository.findByCropId(1L)).thenReturn(Collections.emptyList());
        when(saleRepository.findByCropId(1L)).thenReturn(Collections.emptyList());

        cropService.deleteCrop(1L);
        verify(cropRepository, times(1)).delete(crop);
    }

    @Test
    void deleteCrop_ThrowsExceptionWhenHarvestsExist() {
        Crop crop = new Crop();
        crop.setId(1L);
        when(cropRepository.findById(1L)).thenReturn(Optional.of(crop));
        when(harvestBatchRepository.findByCropId(1L)).thenReturn(List.of(new HarvestBatch()));

        assertThrows(InvalidDataException.class, () -> cropService.deleteCrop(1L));
        verify(cropRepository, never()).delete(crop);
    }
}
