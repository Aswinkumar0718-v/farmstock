package com.farmstock.controller;

import com.farmstock.dto.HarvestBatchRequest;
import com.farmstock.dto.HarvestBatchResponse;
import com.farmstock.service.HarvestBatchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/harvests")
public class HarvestBatchController {

    private final HarvestBatchService harvestBatchService;

    public HarvestBatchController(HarvestBatchService harvestBatchService) {
        this.harvestBatchService = harvestBatchService;
    }

    @PostMapping
    public ResponseEntity<HarvestBatchResponse> createHarvestBatch(@Valid @RequestBody HarvestBatchRequest request) {
        return new ResponseEntity<>(harvestBatchService.createHarvestBatch(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<HarvestBatchResponse>> getAllHarvestBatches() {
        return ResponseEntity.ok(harvestBatchService.getAllHarvestBatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HarvestBatchResponse> getHarvestBatchById(@PathVariable Long id) {
        return ResponseEntity.ok(harvestBatchService.getHarvestBatchById(id));
    }

    @GetMapping("/crop/{cropId}")
    public ResponseEntity<List<HarvestBatchResponse>> getHarvestBatchesByCrop(@PathVariable Long cropId) {
        return ResponseEntity.ok(harvestBatchService.getHarvestBatchesByCrop(cropId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HarvestBatchResponse> updateHarvestBatch(@PathVariable Long id, @Valid @RequestBody HarvestBatchRequest request) {
        return ResponseEntity.ok(harvestBatchService.updateHarvestBatch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHarvestBatch(@PathVariable Long id) {
        harvestBatchService.deleteHarvestBatch(id);
        return ResponseEntity.noContent().build();
    }
}
