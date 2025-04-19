package com.api.central.dto;

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
    private String dish;
    //start of the praparation (dish status IN_PREPARATION)
    private LocalDateTime startTime;
    // preparation finished (dish status FINISHED)
    private LocalDateTime endTime;
}
