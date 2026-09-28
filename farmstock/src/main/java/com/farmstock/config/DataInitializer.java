package com.farmstock.config;

import com.farmstock.entity.Crop;
import com.farmstock.entity.HarvestBatch;
import com.farmstock.entity.Sale;
import com.farmstock.repository.CropRepository;
import com.farmstock.repository.HarvestBatchRepository;
import com.farmstock.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Value("${app.seed-data:true}")
    private boolean seedData;

    @Bean
    public CommandLineRunner initData(CropRepository cropRepository,
                                      HarvestBatchRepository harvestBatchRepository,
                                      SaleRepository saleRepository) {
        return args -> {
            if (seedData && cropRepository.count() == 0) {
                // Crops
                Crop tomato = new Crop();
                tomato.setName("Tomato");
                tomato.setCategory("Vegetable");
                
                Crop potato = new Crop();
                potato.setName("Potato");
                potato.setCategory("Vegetable");
                
                Crop onion = new Crop();
                onion.setName("Onion");
                onion.setCategory("Vegetable");
                
                cropRepository.save(tomato);
                cropRepository.save(potato);
                cropRepository.save(onion);

                // Harvests
                HarvestBatch h1 = new HarvestBatch();
                h1.setCrop(tomato);
                h1.setQuantity(new BigDecimal("100"));
                h1.setHarvestDate(LocalDate.now().minusDays(10));
                
                HarvestBatch h2 = new HarvestBatch();
                h2.setCrop(tomato);
                h2.setQuantity(new BigDecimal("50"));
                h2.setHarvestDate(LocalDate.now().minusDays(5));
                
                HarvestBatch h3 = new HarvestBatch();
                h3.setCrop(potato);
                h3.setQuantity(new BigDecimal("200"));
                h3.setHarvestDate(LocalDate.now().minusDays(8));
                
                HarvestBatch h4 = new HarvestBatch();
                h4.setCrop(onion);
                h4.setQuantity(new BigDecimal("150"));
                h4.setHarvestDate(LocalDate.now().minusDays(7));
                
                harvestBatchRepository.save(h1);
                harvestBatchRepository.save(h2);
                harvestBatchRepository.save(h3);
                harvestBatchRepository.save(h4);

                // Sales
                Sale s1 = new Sale();
                s1.setCrop(tomato);
                s1.setQuantity(new BigDecimal("30"));
                s1.setPricePerUnit(new BigDecimal("50"));
                s1.setSaleDate(LocalDate.now().minusDays(2));
                
                Sale s2 = new Sale();
                s2.setCrop(potato);
                s2.setQuantity(new BigDecimal("50"));
                s2.setPricePerUnit(new BigDecimal("30"));
                s2.setSaleDate(LocalDate.now().minusDays(3));
                
                Sale s3 = new Sale();
                s3.setCrop(onion);
                s3.setQuantity(new BigDecimal("40"));
                s3.setPricePerUnit(new BigDecimal("40"));
                s3.setSaleDate(LocalDate.now().minusDays(1));
                
                saleRepository.save(s1);
                saleRepository.save(s2);
                saleRepository.save(s3);
            }
        };
    }
}
