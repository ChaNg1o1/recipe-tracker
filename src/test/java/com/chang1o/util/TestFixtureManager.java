package com.chang1o.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试夹具管理器 - 负责测试数据的设置和清理
 * 确保每个测试用例都有干净的数据库状态
 */
public class TestFixtureManager {
    private static TestFixtureManager instance;
    private final DatabaseTestUtils dbUtils;
    private final TestDataFactory dataFactory;
    private final List<String> createdTestData;

    private TestFixtureManager() {
        this.dbUtils = DatabaseTestUtils.getInstance();
        this.dataFactory = new TestDataFactory();
        this.createdTestData = new ArrayList<>();
    }

    public static TestFixtureManager getInstance() {
        if (instance == null) {
            instance = new TestFixtureManager();
        }
        return instance;
    }

    /**
     * 设置测试数据库 - 初始化schema和基础数据
     */
    public void setupTestDatabase() {
        try {
            dbUtils.initializeTestSchema();
            insertBasicTestData();
        } catch (SQLException e) {
            throw new RuntimeException("测试数据库设置失败", e);
        }
    }

    /**
     * 清理所有测试数据
     */
    public void cleanupTestData() {
        try {
            dbUtils.truncateAllTables();
            createdTestData.clear();
        } catch (SQLException e) {
            throw new RuntimeException("测试数据清理失败", e);
        }
    }

    /**
     * 创建测试用户
     */
    public int createTestUser(String username, String password) {
        try {
            int userId = dataFactory.createTestUser(username, password);
            createdTestData.add("user:" + userId);
            return userId;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试用户失败", e);
        }
    }

    /**
     * 创建测试食谱
     */
    public int createTestRecipe(String name, int categoryId, int userId) {
        try {
            int recipeId = dataFactory.createTestRecipe(name, categoryId, userId);
            createdTestData.add("recipe:" + recipeId);
            return recipeId;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试食谱失败", e);
        }
    }

    /**
     * 创建测试库存项目
     */
    public int createTestPantryItem(int userId, String ingredientName, String quantity) {
        try {
            // 首先创建食材（如果不存在）
            int ingredientId = dataFactory.createTestIngredient(ingredientName);
            int pantryId = dataFactory.createTestPantryItem(userId, ingredientId, quantity);
            createdTestData.add("pantry:" + pantryId);
            return pantryId;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试库存项目失败", e);
        }
    }

    /**
     * 创建测试分类
     */
    public int createTestCategory(String name) {
        try {
            int categoryId = dataFactory.createTestCategory(name);
            createdTestData.add("category:" + categoryId);
            return categoryId;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试分类失败", e);
        }
    }

    /**
     * 创建测试健康数据
     */
    public int createTestHealthData(int userId, double weight, double height, int age) {
        try {
            int healthDataId = dataFactory.createTestHealthData(userId, weight, height, age);
            createdTestData.add("health_data:" + healthDataId);
            return healthDataId;
        } catch (SQLException e) {
            throw new RuntimeException("创建测试健康数据失败", e);
        }
    }

    /**
     * 插入基础测试数据（分类、食材等）
     */
    private void insertBasicTestData() throws SQLException {
        // 创建基础分类
        dataFactory.createTestCategory("主菜");
        dataFactory.createTestCategory("汤类");
        dataFactory.createTestCategory("甜品");

        // 创建基础食材
        dataFactory.createTestIngredient("鸡肉");
        dataFactory.createTestIngredient("牛肉");
        dataFactory.createTestIngredient("蔬菜");
        dataFactory.createTestIngredient("大米");
    }

    /**
     * 获取创建的测试数据列表（用于调试）
     */
    public List<String> getCreatedTestData() {
        return new ArrayList<>(createdTestData);
    }

    /**
     * 重置管理器实例（测试专用）
     */
    public static void resetInstance() {
        instance = null;
    }
}