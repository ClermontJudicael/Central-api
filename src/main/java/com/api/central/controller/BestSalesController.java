package com.api.central.controller;

import com.api.central.dto.BestSalesResponseDTO;
import com.api.central.service.BestSalesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bestSales")
public class BestSalesController {

    private final BestSalesService bestSalesService;

    public BestSalesController(BestSalesService bestSalesService) {
        this.bestSalesService = bestSalesService;
    }

    @GetMapping
    public ResponseEntity<?> getBestSales(@RequestParam("top") int top) {
        try {
            BestSalesResponseDTO result = bestSalesService.getTopBestSales(top);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error("Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error("Internal server error: " + e.getMessage()));
        }
    }

    private static ErrorResponse error(String message) {
        return new ErrorResponse(message);
    }

    public record ErrorResponse(String message) {}
}
