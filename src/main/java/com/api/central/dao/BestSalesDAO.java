package com.api.central.dao;

import com.api.central.CustomDataSource;
import com.api.central.modele.BestSales;
import com.api.central.modele.Dish;
import com.api.central.modele.SalesPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BestSalesDAO {
    private final CustomDataSource customDataSource;
    private final SalesPointDAO salesPointDAO;
    private final DishDAO dishDAO;

    public List<BestSales> findAll() throws SQLException {
        String query = "SELECT * FROM best_sales";
        try (Connection conn = customDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            List<BestSales> list = new ArrayList<>();
            while (rs.next()) {
                SalesPoint sp = salesPointDAO.findById(rs.getInt("sales_point_id"));
                Dish dish = dishDAO.findById(rs.getInt("dish_id"));

                BestSales bs = new BestSales(
                        rs.getInt("id"),
                        sp,
                        dish,
                        rs.getLong("quantity_sold"),
                        rs.getBigDecimal("total_amount"),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                );
                list.add(bs);
            }
            return list;
        }
    }

    public void save(BestSales bs) throws SQLException {
        String query = "INSERT INTO best_sales (id, sales_point_id, dish_id, quantity_sold, total_amount, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "sales_point_id = EXCLUDED.sales_point_id, " +
                "dish_id = EXCLUDED.dish_id, " +
                "quantity_sold = EXCLUDED.quantity_sold, " +
                "total_amount = EXCLUDED.total_amount, " +
                "updated_at = EXCLUDED.updated_at";

        try (Connection conn = customDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bs.getId());
            stmt.setInt(2, bs.getSalesPoint().getId());
            stmt.setInt(3, bs.getDish().getId());
            stmt.setLong(4, bs.getQuantitySold());
            stmt.setBigDecimal(5, bs.getTotalAmount());
            stmt.setTimestamp(6, Timestamp.valueOf(bs.getUpdatedAt()));
            stmt.executeUpdate();
        }
    }
}