package com.api.central.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SalesElementDTO {
    private String salesPoint;
    private String dish;
    private long quantitySold;
    private BigDecimal totalAmount;
}
