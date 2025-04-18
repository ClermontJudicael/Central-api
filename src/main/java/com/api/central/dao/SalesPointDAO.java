package com.api.central.dao;

import com.api.central.CustomDataSource;
import com.api.central.modele.SalesPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SalesPointDAO {
    private final CustomDataSource customDataSource;

    public List<SalesPoint> findAll() throws SQLException {
        String query = "SELECT id, name FROM sales_point";
        try (Connection conn = customDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<SalesPoint> list = new ArrayList<>();
            while (rs.next()) {
                SalesPoint sp = new SalesPoint(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                list.add(sp);
            }
            return list;
        }
    }

    public SalesPoint findById(int id) throws SQLException {
        String query = "SELECT id, name FROM sales_point WHERE id = ?";
        try (Connection conn = customDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new SalesPoint(
                        rs.getInt("id"),
                        rs.getString("name")
                );
            }
            return null;
        }
    }

    public void save(SalesPoint sp) throws SQLException {
        String query = "INSERT INTO sales_point (id, name) VALUES (?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name";
        try (Connection conn = customDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sp.getId());
            stmt.setString(2, sp.getName());
            stmt.executeUpdate();
        }
    }
}
