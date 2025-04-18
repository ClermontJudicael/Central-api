package com.api.central.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BestSalesResponseDTO {
    private LocalDateTime updatedAt;
    private List<SalesElementDTO> sales;
}
