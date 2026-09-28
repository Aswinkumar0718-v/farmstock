package com.farmstock.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CropRequest {
    @NotBlank(message = "Crop name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;
}
