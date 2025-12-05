package com.chang1o.dao;

import com.chang1o.model.Ingredient;
import com.chang1o.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientDao {

    public boolean addIngredient(Ingredient ingredient) {
        String sql = "INSERT INTO ingredients (name) VALUES (?)";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ingredient.getName());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ingredient.setId(generatedKeys.getInt(1));
                    }
                }
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("添加原料失败：" + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("原料名称已存在，请选择其他名称");
            }
            return false;
        }
    }

    public Ingredient getIngredientById(int ingredientId) {
        String sql = "SELECT id, name FROM ingredients WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ingredientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    return ingredient;
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("根据ID查询原料失败：" + e.getMessage());
            return null;
        }
    }

    public Ingredient getIngredientByName(String name) {
        String sql = "SELECT id, name FROM ingredients WHERE name = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    return ingredient;
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("根据名称查询原料失败：" + e.getMessage());
            return null;
        }
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT id, name FROM ingredients ORDER BY name";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredients.add(ingredient);
            }

        } catch (SQLException e) {
            System.err.println("查询所有原料失败：" + e.getMessage());
        }

        return ingredients;
    }

    public List<Ingredient> searchIngredients(String keyword) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = "SELECT id, name FROM ingredients WHERE name LIKE ? ORDER BY name";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredients.add(ingredient);
                }
            }

        } catch (SQLException e) {
            System.err.println("搜索原料失败：" + e.getMessage());
        }

        return ingredients;
    }

    public boolean updateIngredient(Ingredient ingredient) {
        String sql = "UPDATE ingredients SET name = ? WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ingredient.getName());
            stmt.setInt(2, ingredient.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("更新原料失败：" + e.getMessage());
            return false;
        }
    }

    public boolean deleteIngredient(int ingredientId) {
        String sql = "DELETE FROM ingredients WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ingredientId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("删除原料失败：" + e.getMessage());
            return false;
        }
    }

}
