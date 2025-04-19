package com.api.central.service;

import com.api.central.dao.AggregatedProcessingTimeDAO;
import com.api.central.dao.AggregatedSalesDAO;
import com.api.central.dao.SalesPointDAO;
import com.api.central.dto.DishOrderDTO;
import com.api.central.dto.SaleDTO;
import com.api.central.modele.AggregatedProcessingTime;
import com.api.central.modele.AggregatedSales;
import com.api.central.modele.DurationUnit;
import com.api.central.modele.SalesPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
            AggregatedSales data = new AggregatedSales();
            data.setSalesPointName(sp.getName());
            data.setDish(dto.getDishName());
            data.setQuantitySold(dto.getQuantitySold());
            data.setTotalAmount(Optional.ofNullable(dto.getTotalAmount()).orElse(0.0));
            data.setUpdatedAt(LocalDateTime.now());
            System.out.println("→ Upserting aggregated sale: " + data.getSalesPointName() + ", dish=" + data.getDish());
            aggregatedSalesDAO.upsert(data);
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
            String dish = entry.getKey();
            List<Long> durations = entry.getValue();

            AggregatedProcessingTime data = new AggregatedProcessingTime();
            data.setSalesPointName(sp.getName());
            data.setDish(dish);
            data.setAverage(durationAverage(durations));
            data.setMinimum(Collections.min(durations));
            data.setMaximum(Collections.max(durations));
            data.setUnit(DurationUnit.valueOf("SECONDS"));
            aggregatedProcessingTimeDAO.upsert(data);
        }
    }

    private double durationAverage(List<Long> durations) {
        return durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }
}
