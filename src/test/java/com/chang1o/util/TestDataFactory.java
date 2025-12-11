package com.chang1o.util;

import com.chang1o.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 测试数据工厂 - 创建各种测试数据对象
 */
public class TestDataFactory {
    private final DatabaseTestUtils dbUtils;

    public TestDataFactory() {
        this.dbUtils = DatabaseTestUtils.getInstance();
    }

    /**
     * 创建测试用户
     */
    public int createTestUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("创建用户失败，无法获取生成的ID");
            }
        }
    }

    /**
     * 创建测试用户对象
     */
    public User createTestUser() {
        String username = "testuser_" + System.currentTimeMillis();
        String password = "testpass123";
        
        try {
            int userId = createTestUser(username, password);
            User user = new User();
            user.setId(userId);
            user.setUsername(username);
            user.setPassword(password);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试用户对象失败", e);
        }
    }

    /**
     * 创建测试分类
     */
    public int createTestCategory(String name) throws SQLException {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, name);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("创建分类失败，无法获取生成的ID");
            }
        }
    }

    /**
     * 创建测试分类对象
     */
    public Category createTestCategory() {
        String name = "测试分类_" + System.currentTimeMillis();
        
        try {
            int categoryId = createTestCategory(name);
            Category category = new Category();
            category.setId(categoryId);
            category.setName(name);
            return category;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试分类对象失败", e);
        }
    }

    /**
     * 创建测试食材
     */
    public int createTestIngredient(String name) throws SQLException {
        // 首先检查食材是否已存在
        String checkSql = "SELECT id FROM ingredients WHERE name = ?";
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setString(1, name);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        // 如果不存在，创建新食材
        String sql = "INSERT INTO ingredients (name) VALUES (?)";
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, name);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("创建食材失败，无法获取生成的ID");
            }
        }
    }

    /**
     * 创建测试食材对象
     */
    public Ingredient createTestIngredient() {
        String name = "测试食材_" + System.currentTimeMillis();
        
        try {
            int ingredientId = createTestIngredient(name);
            Ingredient ingredient = new Ingredient();
            ingredient.setId(ingredientId);
            ingredient.setName(name);
            return ingredient;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试食材对象失败", e);
        }
    }

    /**
     * 创建测试食谱
     */
    public int createTestRecipe(String name, int categoryId, int userId) throws SQLException {
        String sql = "INSERT INTO recipes (name, instructions, category_id, user_id) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, name);
            stmt.setString(2, "测试食谱制作说明");
            stmt.setInt(3, categoryId);
            stmt.setInt(4, userId);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("创建食谱失败，无法获取生成的ID");
            }
        }
    }

    /**
     * 创建测试食谱对象
     */
    public Recipe createTestRecipe() {
        try {
            User user = createTestUser();
            Category category = createTestCategory();
            
            String name = "测试食谱_" + System.currentTimeMillis();
            int recipeId = createTestRecipe(name, category.getId(), user.getId());
            
            Recipe recipe = new Recipe();
            recipe.setId(recipeId);
            recipe.setName(name);
            recipe.setInstructions("测试食谱制作说明");
            recipe.setCategoryId(category.getId());
            recipe.setUserId(user.getId());
            return recipe;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试食谱对象失败", e);
        }
    }

    /**
     * 创建测试库存项目
     */
    public int createTestPantryItem(int userId, int ingredientId, String quantity) throws SQLException {
        String sql = "INSERT INTO pantry (user_id, ingredient_id, quantity, expiry_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, ingredientId);
            stmt.setString(3, quantity);
            stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now().plusDays(30)));
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("创建库存项目失败，无法获取生成的ID");
            }
        }
    }

    /**
     * 创建测试库存项目对象
     */
    public PantryItem createTestPantryItem() {
        try {
            User user = createTestUser();
            Ingredient ingredient = createTestIngredient();
            
            int pantryId = createTestPantryItem(user.getId(), ingredient.getId(), "100g");
            
            PantryItem pantryItem = new PantryItem();
            pantryItem.setId(pantryId);
            pantryItem.setUserId(user.getId());
            pantryItem.setIngredientId(ingredient.getId());
            pantryItem.setQuantity("100g");
            pantryItem.setExpiryDate(LocalDate.now().plusDays(30));
            return pantryItem;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试库存项目对象失败", e);
        }
    }

    /**
     * 创建测试健康数据
     */
    public int createTestHealthData(int userId, double weight, double height, int age) throws SQLException {
        String sql = "INSERT INTO user_health_data (user_id, weight, height, age, gender, activity_level, target_weight) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, userId);
            stmt.setDouble(2, weight);
            stmt.setDouble(3, height);
            stmt.setInt(4, age);
            stmt.setString(5, "M");
            stmt.setString(6, "MODERATE");
            stmt.setDouble(7, weight - 5.0);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("创建健康数据失败，无法获取生成的ID");
            }
        }
    }

    /**
     * 创建测试健康数据对象
     */
    public UserHealthData createTestHealthData() {
        try {
            User user = createTestUser();
            
            double weight = 70.0 + ThreadLocalRandom.current().nextDouble(-10, 10);
            double height = 170.0 + ThreadLocalRandom.current().nextDouble(-10, 10);
            int age = 25 + ThreadLocalRandom.current().nextInt(0, 20);
            
            int healthDataId = createTestHealthData(user.getId(), weight, height, age);
            
            UserHealthData healthData = new UserHealthData();
            healthData.setId(healthDataId);
            healthData.setUserId(user.getId());
            healthData.setWeight(weight);
            healthData.setHeight(height);
            healthData.setAge(age);
            healthData.setGender("M");
            healthData.setActivityLevel("MODERATE");
            healthData.setTargetWeight(weight - 5.0);
            return healthData;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试健康数据对象失败", e);
        }
    }

    /**
     * 创建测试每日签到数据
     */
    public DailyCheckIn createTestDailyCheckIn() {
        try {
            User user = createTestUser();
            
            String sql = "INSERT INTO daily_check_in (user_id, check_in_date, mood, sleep_hours, water_intake, exercise_minutes, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = DBUtil.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setInt(1, user.getId());
                stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                stmt.setString(3, "GOOD");
                stmt.setDouble(4, 8.0);
                stmt.setInt(5, 2000);
                stmt.setInt(6, 30);
                stmt.setString(7, "测试签到记录");
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int checkInId = rs.getInt(1);
                        
                        DailyCheckIn checkIn = new DailyCheckIn();
                        checkIn.setId(checkInId);
                        checkIn.setUserId(user.getId());
                        checkIn.setCheckInDate(LocalDate.now());
                        checkIn.setMood("GOOD");
                        checkIn.setSleepHours(8.0);
                        checkIn.setWaterIntake(2000);
                        checkIn.setExerciseMinutes(30);
                        checkIn.setNotes("测试签到记录");
                        return checkIn;
                    }
                    throw new SQLException("创建每日签到失败，无法获取生成的ID");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("创建测试每日签到对象失败", e);
        }
    }

    /**
     * 生成随机测试数据映射
     */
    public Map<String, Object> generateRandomTestData(String tableName) {
        Map<String, Object> data = new HashMap<>();
        
        switch (tableName.toLowerCase()) {
            case "users":
                data.put("username", "user_" + System.currentTimeMillis());
                data.put("password", "password123");
                break;
            case "categories":
                data.put("name", "category_" + System.currentTimeMillis());
                break;
            case "ingredients":
                data.put("name", "ingredient_" + System.currentTimeMillis());
                break;
            default:
                throw new IllegalArgumentException("不支持的表名: " + tableName);
        }
        
        return data;
    }
}