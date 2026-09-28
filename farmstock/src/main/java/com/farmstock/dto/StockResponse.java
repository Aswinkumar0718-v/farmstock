package com.farmstock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
    private Long cropId;
    private String cropName;
    private BigDecimal totalHarvested;
    private BigDecimal totalSold;
    private BigDecimal availableStock;
}
