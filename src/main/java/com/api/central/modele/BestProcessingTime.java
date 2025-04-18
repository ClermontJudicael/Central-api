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
}
