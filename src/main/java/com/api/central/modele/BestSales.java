package com.api.central.modele;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BestSales {
    private int id;
    private SalesPoint salesPoint;
    private Dish dish;
    private Long quantitySold;
    private BigDecimal totalAmount;
    private LocalDateTime updatedAt;
}
