package com.api.central.dao;

import com.api.central.modele.SalesPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SalesPointDAO {
    private final DataSource dataSource;

    @Autowired
    public SalesPointDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<SalesPoint> findAll() throws SQLException {

        String query = "SELECT id, name, baseUrl FROM public.sales_point";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<SalesPoint> list = new ArrayList<>();
            while (rs.next()) {
                SalesPoint sp = new SalesPoint(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("baseUrl")
                );
                list.add(sp);
            }
            return list;
        }
    }

    public SalesPoint findById(int id) throws SQLException {
        String query = "SELECT id, name, baseUrl FROM sales_point WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new SalesPoint(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("baseUrl")
                );
            }
            return null;
        }
    }

    public void save(SalesPoint sp) throws SQLException {
        String query = "INSERT INTO sales_point (id, name) VALUES (?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sp.getId());
            stmt.setString(2, sp.getName());
            stmt.executeUpdate();
        }
    }

    public SalesPoint findByName(String name) throws SQLException {
        String query = "SELECT id, name, baseUrl FROM sales_point WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new SalesPoint(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("baseUrl")
                );
            }
            return null;
        }
    }

}
