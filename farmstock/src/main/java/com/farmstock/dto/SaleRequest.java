package com.farmstock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SaleRequest {
    @NotNull(message = "Crop ID is required")
    private Long cropId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private BigDecimal quantity;

    @NotNull(message = "Price per unit is required")
    @PositiveOrZero(message = "Price per unit cannot be negative")
    private BigDecimal pricePerUnit;

    @NotNull(message = "Sale date is required")
    private LocalDate saleDate;
}
