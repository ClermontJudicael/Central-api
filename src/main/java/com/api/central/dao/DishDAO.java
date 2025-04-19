package com.api.central.dao;

import com.api.central.modele.Dish;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishDAO {
    private final DataSource dataSource;

    @Autowired
    public DishDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Dish> findAll() throws SQLException {
        String query = "SELECT id, name FROM dish";
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
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

    public Dish findByName(String name) throws SQLException {
        String query = "SELECT id, name FROM dish WHERE name ILIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Dish(rs.getInt("id"), rs.getString("name"));
            } else {
                throw new SQLException("Dish not found with name: " + name);
            }
        }
    }

    public BigDecimal findPriceByDishName(String dishName) throws SQLException {
        String query = "SELECT price FROM dish WHERE name ILIKE ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, dishName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("price");
                } else {
                    throw new SQLException("Dish not found: " + dishName);
                }
            }
        }
    }

    public void save(Dish dish) throws SQLException {
        String query = "INSERT INTO dish (id, name) VALUES (?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, dish.getId());
            stmt.setString(2, dish.getName());
            stmt.executeUpdate();
        }
    }
}