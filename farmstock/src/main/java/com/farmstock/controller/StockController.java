package com.farmstock.controller;

import com.farmstock.dto.StockResponse;
import com.farmstock.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllCurrentStock() {
        return ResponseEntity.ok(stockService.getAllCurrentStock());
    }

    @GetMapping("/{cropId}")
    public ResponseEntity<StockResponse> getCurrentStockByCrop(@PathVariable Long cropId) {
        return ResponseEntity.ok(stockService.getStockByCropId(cropId));
    }
}
