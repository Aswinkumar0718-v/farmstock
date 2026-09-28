package com.farmstock.service;

import com.farmstock.dto.CropRequest;
import com.farmstock.dto.CropResponse;
import com.farmstock.entity.Crop;
import com.farmstock.exception.InvalidDataException;
import com.farmstock.exception.ResourceNotFoundException;
import com.farmstock.repository.CropRepository;
import com.farmstock.repository.HarvestBatchRepository;
import com.farmstock.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CropService {
    private final CropRepository cropRepository;
    private final HarvestBatchRepository harvestBatchRepository;
    private final SaleRepository saleRepository;

    public CropService(CropRepository cropRepository, HarvestBatchRepository harvestBatchRepository, SaleRepository saleRepository) {
        this.cropRepository = cropRepository;
        this.harvestBatchRepository = harvestBatchRepository;
        this.saleRepository = saleRepository;
    }

    public CropResponse createCrop(CropRequest request) {
        Crop crop = new Crop();
        crop.setName(request.getName());
        crop.setCategory(request.getCategory());
        Crop savedCrop = cropRepository.save(crop);
        return mapToResponse(savedCrop);
    }

    public List<CropResponse> getAllCrops() {
        return cropRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CropResponse getCropById(Long id) {
        Crop crop = getCropEntityById(id);
        return mapToResponse(crop);
    }

    public CropResponse updateCrop(Long id, CropRequest request) {
        Crop crop = getCropEntityById(id);
        crop.setName(request.getName());
        crop.setCategory(request.getCategory());
        Crop updatedCrop = cropRepository.save(crop);
        return mapToResponse(updatedCrop);
    }

    public void deleteCrop(Long id) {
        Crop crop = getCropEntityById(id);
        
        if (!harvestBatchRepository.findByCropId(id).isEmpty() || !saleRepository.findByCropId(id).isEmpty()) {
            throw new InvalidDataException("Cannot delete crop. It has existing harvest or sale records.");
        }
        
        cropRepository.delete(crop);
    }

    public Crop getCropEntityById(Long id) {
        return cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop with id " + id + " was not found"));
    }

    private CropResponse mapToResponse(Crop crop) {
        CropResponse response = new CropResponse();
        response.setId(crop.getId());
        response.setName(crop.getName());
        response.setCategory(crop.getCategory());
        return response;
    }
}
