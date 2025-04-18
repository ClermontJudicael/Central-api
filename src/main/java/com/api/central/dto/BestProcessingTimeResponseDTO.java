package com.api.central.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BestProcessingTimeResponseDTO {
    private LocalDateTime updatedAt;
    private List<BestProcessingTimeElementDTO> bestProcessingTimes;
}
