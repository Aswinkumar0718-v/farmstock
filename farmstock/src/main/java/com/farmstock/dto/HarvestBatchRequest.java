package com.farmstock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class HarvestBatchRequest {
    @NotNull(message = "Crop ID is required")
    private Long cropId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;

    @NotNull(message = "Harvest date is required")
    private LocalDate harvestDate;
}
