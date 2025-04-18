package com.api.central.dto;

import com.api.central.modele.DurationUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class BestProcessingTimeElementDTO {
    private String salesPoint;
    private String dish;
    private double preparationDuration;
    private DurationUnit durationUnit;
}
