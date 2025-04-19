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
public class AggregatedSales {
    private int id;
    private String salesPointName;
    private String dish;
    private long quantitySold;
    private double totalAmount;
    private LocalDateTime updatedAt;
}

