package com.api.central.dao;

import com.api.central.modele.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.api.central.modele.CalculationMode.*;

@Repository
public class BestProcessingTimeDAO {
    private final DataSource dataSource;
    private final SalesPointDAO salesPointDAO;
    private final DishDAO dishDAO;

    @Autowired
    public BestProcessingTimeDAO(DataSource dataSource,
                                 SalesPointDAO salesPointDAO,
                                 DishDAO dishDAO) {
        this.dataSource = dataSource;
        this.salesPointDAO = salesPointDAO;
        this.dishDAO = dishDAO;
    }

    public void deleteAll() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM best_processing_time");
        }
    }

    public List<BestProcessingTime> findAll() throws SQLException {
        String sql = "SELECT * FROM best_processing_time";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<BestProcessingTime> list = new ArrayList<>();
            while (rs.next()) {
                SalesPoint sp = salesPointDAO.findById(rs.getInt("sales_point_id"));
                Dish dish = dishDAO.findById(rs.getInt("dish_id"));
                list.add(new BestProcessingTime(
                        rs.getInt("id"),
                        sp,
                        dish,
                        rs.getDouble("preparation_duration"),
                        DurationUnit.valueOf(rs.getString("duration_unit")),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                ));
            }
            return list;
        }
    }

    public void save(BestProcessingTime bpt) throws SQLException {
        String query = "INSERT INTO best_processing_time (sales_point_id, dish_id, preparation_duration, duration_unit, updated_at) " +
                "VALUES (?, ?, ?, ?::duration_unit, ?) " +
                "ON CONFLICT (sales_point_id, dish_id) DO UPDATE SET " +
                "preparation_duration = EXCLUDED.preparation_duration, " +
                "duration_unit = EXCLUDED.duration_unit, " +
                "updated_at = EXCLUDED.updated_at";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bpt.getSalesPoint().getId());
            stmt.setInt(2, bpt.getDish().getId());
            stmt.setDouble(3, bpt.getPreparationDuration());
            stmt.setString(4, bpt.getDurationUnit().name());
            stmt.setTimestamp(5, Timestamp.valueOf(bpt.getUpdatedAt()));
            stmt.executeUpdate();
        }
    }

    public List<BestProcessingTime> findTopProcessingTimes(int dishId, int top, CalculationMode mode) throws SQLException {
        String aggregation = switch (mode) {
            case AVERAGE -> "AVG(bpt.preparation_duration)";
            case MINIMUM -> "MIN(bpt.preparation_duration)";
            case MAXIMUM -> "MAX(bpt.preparation_duration)";
        };

        String query = """
            SELECT sp.id AS sp_id, sp.name AS sp_name, sp.baseUrl AS url,
                   d.id AS d_id, d.name AS d_name,
                   %s AS preparation_duration,
                   bpt.duration_unit
            FROM best_processing_time bpt
            JOIN sales_point sp ON bpt.sales_point_id = sp.id
            JOIN dish d ON bpt.dish_id = d.id
            WHERE bpt.dish_id = ?
            GROUP BY sp.id, sp.name, sp.baseUrl, d.id, d.name, bpt.duration_unit
            ORDER BY preparation_duration ASC
            LIMIT ?
        """.formatted(aggregation);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, dishId);
            stmt.setInt(2, top);

            ResultSet rs = stmt.executeQuery();
            List<BestProcessingTime> results = new ArrayList<>();
            while (rs.next()) {
                SalesPoint sp = new SalesPoint(rs.getInt("sp_id"), rs.getString("sp_name"), rs.getString("url"));
                Dish dish = new Dish(rs.getInt("d_id"), rs.getString("d_name"));

                BestProcessingTime entry = new BestProcessingTime(
                        sp,
                        dish,
                        rs.getDouble("preparation_duration"),
                        DurationUnit.valueOf(rs.getString("duration_unit"))
                );
                results.add(entry);
            }
            return results;
        }
    }

    public BestProcessingTime findBySalesPointAndDish(int salesPointId, int dishId) throws SQLException {
        String query = "SELECT * FROM best_processing_time WHERE sales_point_id = ? AND dish_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, salesPointId);
            stmt.setInt(2, dishId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                SalesPoint sp = salesPointDAO.findById(rs.getInt("sales_point_id"));
                Dish dish = dishDAO.findById(rs.getInt("dish_id"));
                return new BestProcessingTime(
                        rs.getInt("id"),
                        sp,
                        dish,
                        rs.getDouble("preparation_duration"),
                        DurationUnit.valueOf(rs.getString("duration_unit")),
                        rs.getTimestamp("updated_at").toLocalDateTime()
                );
            }
            return null;
        }
    }

    public void update(BestProcessingTime bpt) throws SQLException {
        String query = "UPDATE best_processing_time SET " +
                "sales_point_id = ?, " +
                "dish_id = ?, " +
                "preparation_duration = ?, " +
                "duration_unit = ?::duration_unit, " +
                "updated_at = ? " +
                "WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bpt.getSalesPoint().getId());
            stmt.setInt(2, bpt.getDish().getId());
            stmt.setDouble(3, bpt.getPreparationDuration());
            stmt.setString(4, bpt.getDurationUnit().name());
            stmt.setTimestamp(5, Timestamp.valueOf(bpt.getUpdatedAt()));
            stmt.setInt(6, bpt.getId()); // L'ID de la ligne à mettre à jour

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Aucune ligne mise à jour, probablement parce qu'il n'existe pas d'entrée avec cet ID.");
            } else {
                System.out.println("Ligne mise à jour avec succès.");
            }
        }
    }

}
