package com.api.central.service;

import com.api.central.dao.BestSalesDAO;
import com.api.central.dto.BestSalesResponseDTO;
import com.api.central.dto.SalesElementDTO;
import com.api.central.modele.BestSales;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BestSalesService {

    private final BestSalesDAO bestSalesDAO;

    public BestSalesResponseDTO getTopBestSales(int top) throws Exception {
        List<BestSales> allSales = bestSalesDAO.findAll();

        LocalDateTime updatedAt = allSales.stream()
                .map(BestSales::getUpdatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        List<SalesElementDTO> topSales = allSales.stream()
                .sorted(Comparator.comparingLong(BestSales::getQuantitySold).reversed())
                .limit(top)
                .map(bs -> new SalesElementDTO(
                        bs.getSalesPoint().getName(),
                        bs.getDish().getName(),
                        bs.getQuantitySold(),
                        bs.getTotalAmount()
                ))
                .collect(Collectors.toList());

        return new BestSalesResponseDTO(updatedAt, topSales);
    }
}
