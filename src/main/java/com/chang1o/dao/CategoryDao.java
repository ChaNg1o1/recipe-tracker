package com.chang1o.dao;

import com.chang1o.model.Category;
import com.chang1o.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {

    public boolean addCategory(Category category) {
        String sql = "INSERT INTO categories (name) VALUES (?)";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("添加分类失败：" + e.getMessage());
            return false;
        }
    }

    public Category getCategoryById(int categoryId) {
        String sql = "SELECT id, name FROM categories WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    return category;
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("根据ID查询分类失败：" + e.getMessage());
            return null;
        }
    }

    public Category getCategoryByName(String name) {
        String sql = "SELECT id, name FROM categories WHERE name = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    return category;
                }
            }
            return null;

        } catch (SQLException e) {
            System.err.println("根据名称查询分类失败：" + e.getMessage());
            return null;
        }
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT id, name FROM categories ORDER BY name";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                categories.add(category);
            }

        } catch (SQLException e) {
            System.err.println("查询所有分类失败：" + e.getMessage());
        }

        return categories;
    }

    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET name = ? WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category.getName());
            stmt.setInt(2, category.getId());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("更新分类失败：" + e.getMessage());
            return false;
        }
    }

    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM categories WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("删除分类失败：" + e.getMessage());
            return false;
        }
    }

    public boolean exists(int categoryId) {
        return getCategoryById(categoryId) != null;
    }

    public boolean existsByName(String name) {
        return getCategoryByName(name) != null;
    }
}
