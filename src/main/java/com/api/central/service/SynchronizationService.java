package com.api.central.service;

import com.api.central.dao.*;
import com.api.central.dto.DishOrderDTO;
import com.api.central.dto.SaleDTO;
import com.api.central.modele.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SynchronizationService {
    private final RestTemplate restTemplate;
    private final SalesPointDAO salesPointDAO;
    private final AggregatedSalesDAO aggregatedSalesDAO;
    private final AggregatedProcessingTimeDAO aggregatedProcessingTimeDAO;
    private final BestProcessingTimeDAO bestProcessingTimeDAO;
    private final BestSalesDAO bestSalesDAO;
    private final DishDAO dishDAO;

    public void synchronizeAll() throws SQLException {
        List<SalesPoint> salesPoints = salesPointDAO.findAll();
        for (SalesPoint sp : salesPoints) {
            syncSales(sp);
            syncDishOrders(sp);
        }
    }

    private void syncSales(SalesPoint sp) throws SQLException {
        String url = sp.getBaseUrl() + "/sales";
        ResponseEntity<SaleDTO[]> response = restTemplate.getForEntity(url, SaleDTO[].class);
        System.out.println("→ Response from " + url + ": " + Arrays.toString(response.getBody()));
        SaleDTO[] sales = response.getBody();

        for (SaleDTO dto : sales) {
            try {

                BigDecimal price = dishDAO.findPriceByDishName(dto.getDishName());

                BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(dto.getQuantitySold()));

                AggregatedSales data = new AggregatedSales();
                data.setSalesPointName(sp.getName());
                data.setDish(dto.getDishName());
                data.setQuantitySold(dto.getQuantitySold());
                data.setTotalAmount(totalAmount.doubleValue());
                data.setUpdatedAt(LocalDateTime.now());
                System.out.println("→ Upserting aggregated sale: " + data.getSalesPointName() + ", dish=" + data.getDish());
                aggregatedSalesDAO.upsert(data);

                BestSales bestSales = new BestSales();
                Dish dishEntity = dishDAO.findByName(dto.getDishName()); // juste id + name
                bestSales.setSalesPoint(sp);
                bestSales.setDish(dishEntity);
                bestSales.setQuantitySold(dto.getQuantitySold());
                bestSales.setTotalAmount(totalAmount);
                bestSales.setUpdatedAt(LocalDateTime.now());

                bestSalesDAO.upsert(bestSales);

            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération du prix pour " + dto.getDishName() + ": " + e.getMessage());
            }
        }
    }

    private void syncDishOrders(SalesPoint sp) throws SQLException {
        String url = sp.getBaseUrl() + "/dishOrders";
        ResponseEntity<DishOrderDTO[]> response = restTemplate.getForEntity(url, DishOrderDTO[].class);
        DishOrderDTO[] orders = response.getBody();

        Map<String, List<Long>> dishToDurations = new HashMap<>();

        for (DishOrderDTO order : orders) {
            long duration = Duration.between(order.getStartTime(), order.getEndTime()).toSeconds();
            dishToDurations.computeIfAbsent(order.getDish(), k -> new ArrayList<>()).add(duration);
        }

        for (Map.Entry<String, List<Long>> entry : dishToDurations.entrySet()) {
            String dishName = entry.getKey();  // Nom du plat
            List<Long> durations = entry.getValue();

            AggregatedProcessingTime data = new AggregatedProcessingTime();
            data.setSalesPointName(sp.getName());
            data.setDish(dishName);
            data.setAverage(durationAverage(durations));
            data.setMinimum(Collections.min(durations));
            data.setMaximum(Collections.max(durations));
            data.setUnit(DurationUnit.valueOf("SECONDS"));
            data.setUpdatedAt(LocalDateTime.now());
            aggregatedProcessingTimeDAO.upsert(data);

            Dish dishEntity = dishDAO.findByName(dishName);

            BestProcessingTime bestProcessingTime = new BestProcessingTime();
            bestProcessingTime.setSalesPoint(sp);
            bestProcessingTime.setDish(dishEntity);
            bestProcessingTime.setPreparationDuration(durationAverage(durations));
            bestProcessingTime.setDurationUnit(DurationUnit.SECONDS);
            bestProcessingTime.setUpdatedAt(LocalDateTime.now());

            BestProcessingTime existing = bestProcessingTimeDAO.findBySalesPointAndDish(sp.getId(), dishEntity.getId());
            if (existing != null) {
                bestProcessingTimeDAO.update(bestProcessingTime);
            } else {
                bestProcessingTimeDAO.save(bestProcessingTime);
            }
        }
    }

    private double durationAverage(List<Long> durations) {
        return durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }
}
