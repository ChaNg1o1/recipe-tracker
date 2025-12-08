package com.chang1o.dao;

import com.chang1o.model.PantryItem;
import com.chang1o.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PantryDao {

    public boolean addPantryItem(PantryItem item) {
        String sql = "INSERT INTO pantry (user_id, ingredient_id, quantity, expiry_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, item.getUserId());
            stmt.setInt(2, item.getIngredientId());
            stmt.setString(3, item.getQuantity());

            if (item.getExpiryDate() != null) {
                stmt.setDate(4, Date.valueOf(item.getExpiryDate()));
            } else {
                stmt.setNull(4, Types.DATE);
            }

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        item.setId(generatedKeys.getInt(1));
                    }
                }
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("添加库存物品失败：" + e.getMessage());
            return false;
        }
    }

    public PantryItem getPantryItemById(int itemId) {
        String sql = "SELECT id, user_id, ingredient_id, quantity, expiry_date FROM pantry WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PantryItem item = new PantryItem();
                    item.setId(rs.getInt("id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setIngredientId(rs.getInt("ingredient_id"));
                    item.setQuantity(rs.getString("quantity"));

                    Date expiryDate = rs.getDate("expiry_date");
                    if (expiryDate != null) {
                        item.setExpiryDate(expiryDate.toLocalDate());
                    }

                    return item;
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("根据ID查询库存物品失败：" + e.getMessage());
            return null;
        }
    }

    public List<PantryItem> getPantryItemsByUser(int userId) {
        List<PantryItem> items = new ArrayList<>();
        String sql = "SELECT id, user_id, ingredient_id, quantity, expiry_date FROM pantry WHERE user_id = ? ORDER BY expiry_date";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PantryItem item = new PantryItem();
                    item.setId(rs.getInt("id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setIngredientId(rs.getInt("ingredient_id"));
                    item.setQuantity(rs.getString("quantity"));

                    Date expiryDate = rs.getDate("expiry_date");
                    if (expiryDate != null) {
                        item.setExpiryDate(expiryDate.toLocalDate());
                    }

                    items.add(item);
                }
            }

        } catch (SQLException e) {
            System.err.println("获取用户库存失败：" + e.getMessage());
        }

        return items;
    }

    public List<PantryItem> getExpiringItems(int userId, int days) {
        List<PantryItem> items = new ArrayList<>();
        String sql = "SELECT id, user_id, ingredient_id, quantity, expiry_date FROM pantry " +
                    "WHERE user_id = ? AND expiry_date IS NOT NULL " +
                    "AND expiry_date <= DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                    "AND expiry_date >= CURDATE() " +
                    "ORDER BY expiry_date";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, days);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PantryItem item = new PantryItem();
                    item.setId(rs.getInt("id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setIngredientId(rs.getInt("ingredient_id"));
                    item.setQuantity(rs.getString("quantity"));

                    Date expiryDate = rs.getDate("expiry_date");
                    if (expiryDate != null) {
                        item.setExpiryDate(expiryDate.toLocalDate());
                    }

                    items.add(item);
                }
            }

        } catch (SQLException e) {
            System.err.println("获取即将过期物品失败：" + e.getMessage());
        }

        return items;
    }

    public List<PantryItem> getExpiredItems(int userId) {
        List<PantryItem> items = new ArrayList<>();
        String sql = "SELECT id, user_id, ingredient_id, quantity, expiry_date FROM pantry " +
                    "WHERE user_id = ? AND expiry_date IS NOT NULL " +
                    "AND expiry_date < CURDATE() " +
                    "ORDER BY expiry_date";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PantryItem item = new PantryItem();
                    item.setId(rs.getInt("id"));
                    item.setUserId(rs.getInt("user_id"));
                    item.setIngredientId(rs.getInt("ingredient_id"));
                    item.setQuantity(rs.getString("quantity"));

                    Date expiryDate = rs.getDate("expiry_date");
                    if (expiryDate != null) {
                        item.setExpiryDate(expiryDate.toLocalDate());
                    }

                    items.add(item);
                }
            }

        } catch (SQLException e) {
            System.err.println("获取已过期物品失败：" + e.getMessage());
        }

        return items;
    }

    public boolean updatePantryItem(PantryItem item) {
        String sql = "UPDATE pantry SET quantity = ?, expiry_date = ? WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getQuantity());

            if (item.getExpiryDate() != null) {
                stmt.setDate(2, Date.valueOf(item.getExpiryDate()));
            } else {
                stmt.setNull(2, Types.DATE);
            }

            stmt.setInt(3, item.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("更新库存物品失败：" + e.getMessage());
            return false;
        }
    }

    public boolean deletePantryItem(int itemId) {
        String sql = "DELETE FROM pantry WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, itemId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("删除库存物品失败：" + e.getMessage());
            return false;
        }
    }

}