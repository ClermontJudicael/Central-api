package com.api.central.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
// looks the same as SalesElement, but this one is for the synchronization
public class SaleDTO {
    @JsonProperty("dishIdentifier")
    private Integer dishId;

    @JsonProperty("dishName")
    private String dishName;

    @JsonProperty("quantitySold")
    private Long quantitySold;

    @Override
    public String toString() {
        return "SaleDTO{dish='%s', quantitySold=%d, totalAmount=%d}".formatted(dishName, quantitySold, quantitySold);
    }

    public double getTotalAmount() {
        return 0.0;
    }
}
