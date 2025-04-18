package com.api.central.dao;

import com.api.central.CustomDataSource;
import com.api.central.modele.DurationUnit;
import com.api.central.modele.SalesPoint;
import com.api.central.modele.Dish;
import com.api.central.modele.BestProcessingTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BestProcessingTimeDAO {
    private final CustomDataSource customDataSource;
    private final SalesPointDAO salesPointDAO;
    private final DishDAO dishDAO;

    public List<BestProcessingTime> findAll() throws SQLException {
        String query = "SELECT * FROM best_processing_time";
        try (Connection conn = customDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            List<BestProcessingTime> list = new ArrayList<>();
            while (rs.next()) {
                SalesPoint sp = salesPointDAO.findById(rs.getInt("sales_point_id"));
                Dish dish = dishDAO.findById(rs.getInt("dish_id"));

                BestProcessingTime bpt = new BestProcessingTime(
                        rs.getInt("id"),
                        sp,
                        dish,
                        rs.getDouble("preparation_duration"),
                        DurationUnit.valueOf(rs.getString("duration_unit")),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                );
                list.add(bpt);
            }
            return list;
        }
    }

    public void save(BestProcessingTime bpt) throws SQLException {
        String query = "INSERT INTO best_processing_time (id, sales_point_id, dish_id, preparation_duration, duration_unit, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET " +
                "sales_point_id = EXCLUDED.sales_point_id, " +
                "dish_id = EXCLUDED.dish_id, " +
                "preparation_duration = EXCLUDED.preparation_duration, " +
                "duration_unit = EXCLUDED.duration_unit, " +
                "updated_at = EXCLUDED.updated_at";

        try (Connection conn = customDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bpt.getId());
            stmt.setInt(2, bpt.getSalesPoint().getId());
            stmt.setInt(3, bpt.getDish().getId());
            stmt.setDouble(4, bpt.getPreparationDuration());
            stmt.setString(5, bpt.getDurationUnit().name());
            stmt.setTimestamp(6, Timestamp.valueOf(bpt.getUpdatedAt()));
            stmt.executeUpdate();
        }
    }
}

