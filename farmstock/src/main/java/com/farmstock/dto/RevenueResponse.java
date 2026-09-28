package com.farmstock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueResponse {
    private Long cropId;
    private String cropName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalRevenue;
}
