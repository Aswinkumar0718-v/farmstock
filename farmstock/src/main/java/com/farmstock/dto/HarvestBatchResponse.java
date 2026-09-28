package com.farmstock.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HarvestBatchResponse {
    private Long id;
    private Long cropId;
    private String cropName;
    private BigDecimal quantity;
    private LocalDate harvestDate;
}
