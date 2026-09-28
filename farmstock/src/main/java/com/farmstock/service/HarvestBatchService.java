package com.farmstock.service;

import com.farmstock.dto.HarvestBatchRequest;
import com.farmstock.dto.HarvestBatchResponse;
import com.farmstock.entity.Crop;
import com.farmstock.entity.HarvestBatch;
import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.repository.HarvestBatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HarvestBatchService {

    private final HarvestBatchRepository harvestBatchRepository;
    private final CropService cropService;

    public HarvestBatchService(HarvestBatchRepository harvestBatchRepository, CropService cropService) {
        this.harvestBatchRepository = harvestBatchRepository;
        this.cropService = cropService;
    }

    public HarvestBatchResponse createHarvestBatch(HarvestBatchRequest request) {
        Crop crop = cropService.getCropEntityById(request.getCropId());
        
        HarvestBatch batch = new HarvestBatch();
        batch.setCrop(crop);
        batch.setQuantity(request.getQuantity());
        batch.setHarvestDate(request.getHarvestDate());
        
        HarvestBatch savedBatch = harvestBatchRepository.save(batch);
        return mapToResponse(savedBatch);
    }

    public List<HarvestBatchResponse> getAllHarvestBatches() {
        return harvestBatchRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public HarvestBatchResponse getHarvestBatchById(Long id) {
        HarvestBatch batch = harvestBatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HarvestBatch with id " + id + " was not found"));
        return mapToResponse(batch);
    }

    public List<HarvestBatchResponse> getHarvestBatchesByCrop(Long cropId) {
        // validate crop exists
        cropService.getCropEntityById(cropId);
        return harvestBatchRepository.findByCropId(cropId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public HarvestBatchResponse updateHarvestBatch(Long id, HarvestBatchRequest request) {
        HarvestBatch batch = harvestBatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HarvestBatch with id " + id + " was not found"));
        
        Crop crop = cropService.getCropEntityById(request.getCropId());
        batch.setCrop(crop);
        batch.setQuantity(request.getQuantity());
        batch.setHarvestDate(request.getHarvestDate());
        
        HarvestBatch updatedBatch = harvestBatchRepository.save(batch);
        return mapToResponse(updatedBatch);
    }

    public void deleteHarvestBatch(Long id) {
        HarvestBatch batch = harvestBatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HarvestBatch with id " + id + " was not found"));
        harvestBatchRepository.delete(batch);
    }

    private HarvestBatchResponse mapToResponse(HarvestBatch batch) {
        HarvestBatchResponse response = new HarvestBatchResponse();
        response.setId(batch.getId());
        response.setCropId(batch.getCrop().getId());
        response.setCropName(batch.getCrop().getName());
        response.setQuantity(batch.getQuantity());
        response.setHarvestDate(batch.getHarvestDate());
        return response;
    }
}
