package com.api.central.dao;

import com.api.central.CustomDataSource;
import com.api.central.modele.AggregatedProcessingTime;
import com.api.central.modele.DurationUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

@RequiredArgsConstructor
@Repository
public class AggregatedProcessingTimeDAO {

    private final CustomDataSource customDataSource;

    public void upsert(AggregatedProcessingTime time) throws SQLException {
        String sql = """
            INSERT INTO aggregated_processing_time 
                (sales_point_name, dish, average, minimum, maximum, unit, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (sales_point_name, dish)
            DO UPDATE SET
                average = EXCLUDED.average,
                minimum = EXCLUDED.minimum,
                maximum = EXCLUDED.maximum,
                unit = EXCLUDED.unit,
                updated_at = EXCLUDED.updated_at
        """;

        try (Connection connection = customDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, time.getSalesPointName());
            stmt.setString(2, time.getDish());
            stmt.setDouble(3, time.getAverage());
            stmt.setLong(4, time.getMinimum());
            stmt.setLong(5, time.getMaximum());
            stmt.setString(6, time.getUnit().name());
            stmt.setTimestamp(7, Timestamp.valueOf(time.getUpdatedAt()));
            stmt.executeUpdate();
        }
    }

    public List<AggregatedProcessingTime> findAll() throws SQLException {
        String sql = "SELECT * FROM aggregated_processing_time";
        List<AggregatedProcessingTime> list = new ArrayList<>();

        try (Connection connection = customDataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                AggregatedProcessingTime time = new AggregatedProcessingTime();
                time.setId(rs.getInt("id"));
                time.setSalesPointName(rs.getString("sales_point_name"));
                time.setDish(rs.getString("dish"));
                time.setAverage(rs.getDouble("average"));
                time.setMinimum(rs.getLong("minimum"));
                time.setMaximum(rs.getLong("maximum"));
                time.setUnit(DurationUnit.valueOf(rs.getString("unit")));
                time.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                list.add(time);
            }
        }
        return list;
    }
}