package com.farmstock.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Crop name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    @OneToMany(mappedBy = "crop", fetch = FetchType.LAZY)
    private List<HarvestBatch> harvestBatches;

    @OneToMany(mappedBy = "crop", fetch = FetchType.LAZY)
    private List<Sale> sales;
}
