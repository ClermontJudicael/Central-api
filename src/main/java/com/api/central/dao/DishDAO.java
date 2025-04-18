package com.api.central.dao;

import com.api.central.CustomDataSource;
import com.api.central.modele.Dish;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DishDAO {
    private final CustomDataSource customDataSource;

    public List<Dish> findAll() throws SQLException {
        String query = "SELECT id, name FROM dish";
        try (Connection conn = customDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            List<Dish> list = new ArrayList<>();
            while (rs.next()) {
                Dish dish = new Dish(
                        rs.getInt("id"),
                        rs.getString("name")
                );
                list.add(dish);
            }
            return list;
        }
    }

    public Dish findById(int id) throws SQLException {
        String query = "SELECT id, name FROM dish WHERE id = ?";
        try (Connection conn = customDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Dish(
                        rs.getInt("id"),
                        rs.getString("name")
                );
            }
            return null;
        }
    }

    public void save(Dish dish) throws SQLException {
        String query = "INSERT INTO dish (id, name) VALUES (?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name";
        try (Connection conn = customDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, dish.getId());
            stmt.setString(2, dish.getName());
            stmt.executeUpdate();
        }
    }
}