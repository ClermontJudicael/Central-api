package com.api.central.service;

import com.api.central.dao.BestProcessingTimeDAO;
import com.api.central.dao.DishDAO;
import com.api.central.dao.SalesPointDAO;
import com.api.central.dto.BestProcessingTimeElementDTO;
import com.api.central.dto.BestProcessingTimeResponseDTO;
import com.api.central.modele.BestProcessingTime;
import com.api.central.modele.CalculationMode;
import com.api.central.modele.DurationUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BestProcessingTimeService {

    private final BestProcessingTimeDAO bestProcessingTimeDAO;
    private final SalesPointDAO salesPointDAO;
    private final DishDAO dishDAO;

    public BestProcessingTimeResponseDTO getBestProcessingTimes(int dishId, int top, DurationUnit unit, CalculationMode mode) throws SQLException {
        List<BestProcessingTime> entries = bestProcessingTimeDAO.findTopProcessingTimes(dishId, top, mode);

        // Normalisation
        List<BestProcessingTimeElementDTO> dtos = entries.stream().map(entry -> {
            double normalized = normalize(entry.getPreparationDuration(), entry.getDurationUnit(), unit);
            return new BestProcessingTimeElementDTO(
                    entry.getSalesPointName(),
                    entry.getDishName(),
                    normalized,
                    unit
            );
        }).collect(Collectors.toList());

        return new BestProcessingTimeResponseDTO(LocalDateTime.now(), dtos);
    }

    private double normalize(double value, DurationUnit from, DurationUnit to) {
        int seconds = switch (from) {
            case SECONDS -> 1;
            case MINUTES -> 60;
            case HOUR -> 3600;
        };
        int toSeconds = switch (to) {
            case SECONDS -> 1;
            case MINUTES -> 60;
            case HOUR -> 3600;
        };
        return (value * seconds) / toSeconds;
    }
}