package com.api.central.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
// looks the same as SalesElement, but this one is for the synchronization
public class SaleDTO {
    private String dish;
    private Long quantitySold;
    private Double totalAmount;
}
