package com.chang1o.dao;

import com.chang1o.model.Recipe;
import com.chang1o.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeDao {

    public boolean addRecipe(Recipe recipe) {
        String sql = "INSERT INTO recipes (name, instructions, category_id, user_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, recipe.getName());
            stmt.setString(2, recipe.getInstructions());
            stmt.setInt(3, recipe.getCategoryId());
            stmt.setInt(4, recipe.getUserId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        recipe.setId(generatedKeys.getInt(1));
                    }
                }
            }

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("添加食谱失败：" + e.getMessage());
            return false;
        }
    }

    public Recipe getRecipeById(int recipeId) {
        String sql = "SELECT id, name, instructions, category_id, user_id FROM recipes WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("id"));
                    recipe.setName(rs.getString("name"));
                    recipe.setInstructions(rs.getString("instructions"));
                    recipe.setCategoryId(rs.getInt("category_id"));
                    recipe.setUserId(rs.getInt("user_id"));
                    return recipe;
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("根据ID查询食谱失败：" + e.getMessage());
            return null;
        }
    }

    public Recipe getRecipeByName(String name) {
        String sql = "SELECT id, name, instructions, category_id, user_id FROM recipes WHERE name = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("id"));
                    recipe.setName(rs.getString("name"));
                    recipe.setInstructions(rs.getString("instructions"));
                    recipe.setCategoryId(rs.getInt("category_id"));
                    recipe.setUserId(rs.getInt("user_id"));
                    return recipe;
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("根据名称查询食谱失败：" + e.getMessage());
            return null;
        }
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT id, name, instructions, category_id, user_id FROM recipes ORDER BY name";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setName(rs.getString("name"));
                recipe.setInstructions(rs.getString("instructions"));
                recipe.setCategoryId(rs.getInt("category_id"));
                recipe.setUserId(rs.getInt("user_id"));
                recipes.add(recipe);
            }

        } catch (SQLException e) {
            System.err.println("查询所有食谱失败：" + e.getMessage());
        }

        return recipes;
    }

    public List<Recipe> getRecipesByCategory(int categoryId) {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT id, name, instructions, category_id, user_id FROM recipes WHERE category_id = ? ORDER BY name";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("id"));
                    recipe.setName(rs.getString("name"));
                    recipe.setInstructions(rs.getString("instructions"));
                    recipe.setCategoryId(rs.getInt("category_id"));
                    recipe.setUserId(rs.getInt("user_id"));
                    recipes.add(recipe);
                }
            }

        } catch (SQLException e) {
            System.err.println("根据分类查询食谱失败：" + e.getMessage());
        }

        return recipes;
    }

    public List<Recipe> searchRecipes(String keyword) {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT id, name, instructions, category_id, user_id FROM recipes WHERE name LIKE ? ORDER BY name";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("id"));
                    recipe.setName(rs.getString("name"));
                    recipe.setInstructions(rs.getString("instructions"));
                    recipe.setCategoryId(rs.getInt("category_id"));
                    recipe.setUserId(rs.getInt("user_id"));
                    recipes.add(recipe);
                }
            }

        } catch (SQLException e) {
            System.err.println("搜索食谱失败：" + e.getMessage());
        }

        return recipes;
    }

    public List<Recipe> getRecipesByUser(int userId) {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT id, name, instructions, category_id, user_id FROM recipes WHERE user_id = ? ORDER BY name";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("id"));
                    recipe.setName(rs.getString("name"));
                    recipe.setInstructions(rs.getString("instructions"));
                    recipe.setCategoryId(rs.getInt("category_id"));
                    recipe.setUserId(rs.getInt("user_id"));
                    recipes.add(recipe);
                }
            }

        } catch (SQLException e) {
            System.err.println("查询用户食谱失败：" + e.getMessage());
        }

        return recipes;
    }

    public boolean updateRecipe(Recipe recipe) {
        String sql = "UPDATE recipes SET name = ?, instructions = ?, category_id = ?, user_id = ? WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, recipe.getName());
            stmt.setString(2, recipe.getInstructions());
            stmt.setInt(3, recipe.getCategoryId());
            stmt.setInt(4, recipe.getUserId());
            stmt.setInt(5, recipe.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("更新食谱失败：" + e.getMessage());
            return false;
        }
    }

    public boolean deleteRecipe(int recipeId) {
        String sql = "DELETE FROM recipes WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipeId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("删除食谱失败：" + e.getMessage());
            return false;
        }
    }

    public boolean exists(int recipeId) {
        return getRecipeById(recipeId) != null;
    }
}
