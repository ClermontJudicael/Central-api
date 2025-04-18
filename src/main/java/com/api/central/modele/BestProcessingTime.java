package com.api.central.modele;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Service
public class BestProcessingTime {
    private int id;
    private SalesPoint salesPoint;
    private Dish dish;
    private double preparationDuration;
    private DurationUnit durationUnit;
    private LocalDateTime updatedAt;

    public BestProcessingTime(SalesPoint salesPoint, Dish dish, double preparationDuration, DurationUnit durationUnit) {
        this.salesPoint = salesPoint;
        this.dish = dish;
        this.preparationDuration = preparationDuration;
        this.durationUnit = durationUnit;
    }

    public String getSalesPointName() {
        return this.salesPoint.getName();
    }

    public String getDishName() {
        return this.dish.getName();
    }
}
