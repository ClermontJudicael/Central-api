package com.api.central.dao;

import com.api.central.modele.BestSales;
import com.api.central.modele.Dish;
import com.api.central.modele.SalesPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BestSalesDAO {
    private final DataSource dataSource;
    private final SalesPointDAO salesPointDAO;
    private final DishDAO dishDAO;

    @Autowired
    public BestSalesDAO(DataSource dataSource, SalesPointDAO salesPointDAO, DishDAO dishDAO) {
        this.dataSource = dataSource;
        this.salesPointDAO = salesPointDAO;
        this.dishDAO = dishDAO;
    }

    public List<BestSales> findAll() throws SQLException {
        String query = "SELECT * FROM best_sales";
        try (Connection conn = dataSource.getConnection();
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

    public void upsert(BestSales bestSales) throws SQLException {
        String sql = """
    INSERT INTO best_sales (sales_point_id, dish_id, quantity_sold, total_amount, updated_at)
    VALUES (?, ?, ?, ?, ?)
    ON CONFLICT (sales_point_id, dish_id)
    DO UPDATE SET
        quantity_sold = EXCLUDED.quantity_sold,
        total_amount = EXCLUDED.total_amount,
        updated_at = EXCLUDED.updated_at;
    """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bestSales.getSalesPoint().getId());
            stmt.setInt(2, bestSales.getDish().getId());
            stmt.setLong(3, bestSales.getQuantitySold());
            stmt.setBigDecimal(4, bestSales.getTotalAmount());
            stmt.setTimestamp(5, Timestamp.valueOf(bestSales.getUpdatedAt()));
            stmt.executeUpdate();
        }
    }

}
