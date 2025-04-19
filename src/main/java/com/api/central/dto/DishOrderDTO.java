package com.api.central.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DishOrderDTO {

    @JsonProperty("dishName")
    private String dish;

    @JsonProperty("inPreparationDate")
    private LocalDateTime startTime;

    @JsonProperty("finishedDate")
    private LocalDateTime endTime;
}
