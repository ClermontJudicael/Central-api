package com.api.central.controller;

import com.api.central.dto.BestProcessingTimeResponseDTO;
import com.api.central.modele.CalculationMode;
import com.api.central.modele.DurationUnit;
import com.api.central.service.BestProcessingTimeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dishes")
public class BestProcessingTimeController {

    private final BestProcessingTimeService service;

    public BestProcessingTimeController(BestProcessingTimeService service) {
        this.service = service;
    }

    @GetMapping("/{id}/bestProcessingTime")
    public ResponseEntity<?> getBestProcessingTime(
            @PathVariable("id") int dishId,
            @RequestParam("top") int top,
            @RequestParam(value = "durationUnit", defaultValue = "SECONDS") DurationUnit unit,
            @RequestParam(value = "calculationMode", defaultValue = "AVERAGE") CalculationMode mode
    ) {
        try {
            BestProcessingTimeResponseDTO response = service.getBestProcessingTimes(dishId, top, unit, mode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to fetch best processing time: " + e.getMessage()));
        }
    }

    public record ErrorResponse(String message) {}
}

