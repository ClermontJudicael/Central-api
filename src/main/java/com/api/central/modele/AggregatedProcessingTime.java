package com.api.central.modele;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AggregatedProcessingTime {
    private int id;
    private String salesPointName;
    private String dish;
    private double average;
    private long minimum;
    private long maximum;
    private DurationUnit unit;
    private LocalDateTime updatedAt;
}
