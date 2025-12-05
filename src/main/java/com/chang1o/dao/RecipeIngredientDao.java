package com.chang1o.dao;

import com.chang1o.model.RecipeIngredient;
import com.chang1o.model.Ingredient;
import com.chang1o.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientDao {

    private IngredientDao ingredientDao;

    public RecipeIngredientDao() {
        this.ingredientDao = new IngredientDao();
    }

    public boolean addRecipeIngredient(RecipeIngredient recipeIngredient) {
        String sql = "INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, recipeIngredient.getRecipeId());
            pstmt.setInt(2, recipeIngredient.getIngredientId());
            pstmt.setString(3, recipeIngredient.getQuantity());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("添加食谱食材关联失败：" + e.getMessage());
            return false;
        }
    }

    public boolean addRecipeIngredients(int recipeId, List<RecipeIngredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return true;
        }

        String sql = "INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (RecipeIngredient ingredient : ingredients) {
                pstmt.setInt(1, recipeId);
                pstmt.setInt(2, ingredient.getIngredientId());
                pstmt.setString(3, ingredient.getQuantity());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();

            return true;

        } catch (SQLException e) {
            System.err.println("批量添加食谱食材关联失败：" + e.getMessage());
            return false;
        }
    }

    public List<RecipeIngredient> getIngredientsByRecipeId(int recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        String sql = "SELECT * FROM recipe_ingredients WHERE recipe_id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, recipeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    RecipeIngredient recipeIngredient = new RecipeIngredient();
                    recipeIngredient.setRecipeId(rs.getInt("recipe_id"));
                    recipeIngredient.setIngredientId(rs.getInt("ingredient_id"));
                    recipeIngredient.setQuantity(rs.getString("quantity"));

                    Ingredient ingredient = ingredientDao.getIngredientById(recipeIngredient.getIngredientId());
                    recipeIngredient.setIngredient(ingredient);

                    ingredients.add(recipeIngredient);
                }
            }

        } catch (SQLException e) {
            System.err.println("获取食谱食材失败：" + e.getMessage());
        }

        return ingredients;
    }

    public boolean deleteIngredientsByRecipeId(int recipeId) {
        String sql = "DELETE FROM recipe_ingredients WHERE recipe_id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, recipeId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("删除食谱食材关联失败：" + e.getMessage());
            return false;
        }
    }

    public boolean updateRecipeIngredients(int recipeId, List<RecipeIngredient> ingredients) {
        deleteIngredientsByRecipeId(recipeId);
        return addRecipeIngredients(recipeId, ingredients);
    }

    public boolean recipeHasIngredient(int recipeId, int ingredientId) {
        String sql = "SELECT COUNT(*) FROM recipe_ingredients WHERE recipe_id = ? AND ingredient_id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, ingredientId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("检查食谱食材失败：" + e.getMessage());
        }

        return false;
    }

    public int countRecipesByIngredient(int ingredientId) {
        String sql = "SELECT COUNT(DISTINCT recipe_id) FROM recipe_ingredients WHERE ingredient_id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ingredientId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("统计使用食材的食谱数量失败：" + e.getMessage());
        }

        return 0;
    }
}
