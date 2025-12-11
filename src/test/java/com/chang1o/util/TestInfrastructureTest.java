package com.chang1o.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试基础设施验证测试
 * 验证测试工具类是否正常工作
 */
class TestInfrastructureTest {

    private TestFixtureManager fixtureManager;
    private DatabaseTestUtils dbUtils;
    private TestDataFactory dataFactory;
    private MockConfigurationManager mockManager;

    @BeforeEach
    void setUp() {
        fixtureManager = TestFixtureManager.getInstance();
        dbUtils = DatabaseTestUtils.getInstance();
        dataFactory = new TestDataFactory();
        mockManager = MockConfigurationManager.getInstance();
        
        // 设置测试数据库
        fixtureManager.setupTestDatabase();
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        fixtureManager.cleanupTestData();
        mockManager.resetAllMocks();
    }

    @Test
    void testDatabaseTestUtilsInitialization() throws SQLException {
        // 验证数据库工具初始化
        assertNotNull(dbUtils);
        
        // 验证表是否存在
        assertTrue(dbUtils.tableExists("users"));
        assertTrue(dbUtils.tableExists("categories"));
        assertTrue(dbUtils.tableExists("ingredients"));
        assertTrue(dbUtils.tableExists("recipes"));
        assertTrue(dbUtils.tableExists("pantry"));
    }

    @Test
    void testTestDataFactoryCreation() {
        // 验证测试数据工厂能创建对象
        assertNotNull(dataFactory);
        
        // 测试创建用户
        int userId = fixtureManager.createTestUser("testuser", "password");
        assertTrue(userId > 0);
        
        // 测试创建分类
        int categoryId = fixtureManager.createTestCategory("测试分类");
        assertTrue(categoryId > 0);
    }

    @Test
    void testMockConfigurationManager() {
        // 验证Mock配置管理器
        assertNotNull(mockManager);
        
        // 测试创建Mock Scanner
        String testInput = "test input";
        var mockScanner = mockManager.createMockScanner(testInput);
        assertNotNull(mockScanner);
    }

    @Test
    void testTestFixtureManagerDataCreation() {
        // 测试完整的数据创建流程
        int userId = fixtureManager.createTestUser("testuser2", "password2");
        int categoryId = fixtureManager.createTestCategory("测试分类2");
        int recipeId = fixtureManager.createTestRecipe("测试食谱", categoryId, userId);
        int pantryId = fixtureManager.createTestPantryItem(userId, "测试食材", "100g");
        int healthDataId = fixtureManager.createTestHealthData(userId, 70.0, 175.0, 25);
        
        // 验证所有ID都是有效的
        assertTrue(userId > 0);
        assertTrue(categoryId > 0);
        assertTrue(recipeId > 0);
        assertTrue(pantryId > 0);
        assertTrue(healthDataId > 0);
        
        // 验证创建的数据被记录
        assertFalse(fixtureManager.getCreatedTestData().isEmpty());
    }

    @Test
    void testDatabaseOperations() throws SQLException {
        // 测试数据库操作
        int initialUserCount = dbUtils.countRecords("users");
        
        // 创建用户
        int userId = fixtureManager.createTestUser("dbtest", "password");
        
        // 验证用户数量增加
        int newUserCount = dbUtils.countRecords("users");
        assertEquals(initialUserCount + 1, newUserCount);
        
        // 清理数据
        fixtureManager.cleanupTestData();
        
        // 验证数据被清理（基础数据除外）
        int finalUserCount = dbUtils.countRecords("users");
        assertEquals(0, finalUserCount);
    }
}