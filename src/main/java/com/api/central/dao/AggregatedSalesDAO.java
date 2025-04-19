package com.api.central.dao;

import com.api.central.CustomDataSource;
import com.api.central.modele.AggregatedSales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class AggregatedSalesDAO {
    private final CustomDataSource customDataSource;

    public void upsert(AggregatedSales sales) throws SQLException {
        String sql = """
            INSERT INTO aggregated_sales (sales_point_name, dish, quantity_sold, total_amount, updated_at)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (sales_point_name, dish)
            DO UPDATE SET
                quantity_sold = EXCLUDED.quantity_sold,
                total_amount = EXCLUDED.total_amount,
                updated_at = EXCLUDED.updated_at
        """;

        try (Connection connection = customDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sales.getSalesPointName());
            stmt.setString(2, sales.getDish());
            stmt.setLong(3, sales.getQuantitySold());
            stmt.setDouble(4, sales.getTotalAmount());
            stmt.setTimestamp(5, Timestamp.valueOf(sales.getUpdatedAt()));
            stmt.executeUpdate();
        }
    }

    public List<AggregatedSales> findAll() throws SQLException {
        String sql = "SELECT * FROM aggregated_sales";
        List<AggregatedSales> list = new ArrayList<>();

        try (Connection connection = customDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AggregatedSales sales = new AggregatedSales();
                sales.setId(rs.getInt("id"));
                sales.setSalesPointName(rs.getString("sales_point_name"));
                sales.setDish(rs.getString("dish"));
                sales.setQuantitySold(rs.getLong("quantity_sold"));
                sales.setTotalAmount(rs.getDouble("total_amount"));
                sales.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                list.add(sales);
            }
        }
        return list;
    }
}
