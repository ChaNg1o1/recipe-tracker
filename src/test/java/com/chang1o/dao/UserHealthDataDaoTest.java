package com.chang1o.dao;

import com.chang1o.model.UserHealthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * UserHealthDataDao单元测试
 * 使用真实的数据库连接进行测试，确保数据库状态正确隔离
 */
class UserHealthDataDaoTest {

    private UserHealthDataDao userHealthDataDao;

    @BeforeEach
    void setUp() {
        // 设置测试数据库配置
        System.setProperty("DB_CONFIG", "test-database.properties");
        // 重置DBUtil实例以强制重新加载测试配置
        com.chang1o.util.DBUtil.resetInstance();
        userHealthDataDao = new UserHealthDataDao();
        cleanDatabase();
        initializeTestSchema();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        try (var conn = com.chang1o.util.DBUtil.getInstance().getConnection();
             var stmt = conn.createStatement()) {
            
            // 简单清理数据
            try {
                stmt.execute("DELETE FROM user_health_data");
            } catch (Exception e) {
                // 表可能不存在，忽略错误
            }
            
            try {
                stmt.execute("DELETE FROM users");
            } catch (Exception e) {
                // 表可能不存在，忽略错误
            }
            
        } catch (Exception e) {
            System.err.println("清理测试数据库失败: " + e.getMessage());
        }
    }

    private void initializeTestSchema() {
        try (var conn = com.chang1o.util.DBUtil.getInstance().getConnection();
             var stmt = conn.createStatement()) {
            
            // 创建必要的表
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL
                )
            """);
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS user_health_data (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    user_id INT NOT NULL,
                    weight DECIMAL(5,2),
                    height DECIMAL(5,2),
                    age INT,
                    gender VARCHAR(1),
                    activity_level VARCHAR(20),
                    target_weight DECIMAL(5,2),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
        } catch (Exception e) {
            System.err.println("初始化测试数据库schema失败: " + e.getMessage());
        }
    }

    private void setupTestData() {
        // 先创建用户
        com.chang1o.dao.UserDao userDao = new com.chang1o.dao.UserDao();
        com.chang1o.model.User user1 = createTestUser(0, "testuser1", "password123");
        com.chang1o.model.User user2 = createTestUser(0, "testuser2", "password456");
        userDao.addUser(user1);
        userDao.addUser(user2);

        // 创建测试健康数据
        UserHealthData healthData1 = createTestHealthData(0, 1, 70.0, 175.0, 30, "M", "moderate", 68.0);
        UserHealthData healthData2 = createTestHealthData(0, 1, 72.0, 175.0, 30, "M", "moderate", 68.0);
        UserHealthData healthData3 = createTestHealthData(0, 2, 65.0, 170.0, 25, "F", "active", 63.0);

        userHealthDataDao.addHealthData(healthData1);
        userHealthDataDao.addHealthData(healthData2);
        userHealthDataDao.addHealthData(healthData3);
    }

    private com.chang1o.model.User createTestUser(int id, String username, String password) {
        com.chang1o.model.User user = new com.chang1o.model.User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    private UserHealthData createTestHealthData(int id, int userId, double weight, double height, int age, String gender, String activityLevel, double targetWeight) {
        UserHealthData healthData = new UserHealthData();
        healthData.setId(id);
        healthData.setUserId(userId);
        healthData.setWeight(weight);
        healthData.setHeight(height);
        healthData.setAge(age);
        healthData.setGender(gender);
        healthData.setActivityLevel(activityLevel);
        healthData.setTargetWeight(targetWeight);
        return healthData;
    }

    @Test
    void testAddHealthDataSuccess() {
        // Given - 先创建用户
        com.chang1o.dao.UserDao userDao = new com.chang1o.dao.UserDao();
        com.chang1o.model.User user = createTestUser(0, "testuser", "password123");
        userDao.addUser(user);

        UserHealthData healthData = createTestHealthData(0, 1, 70.0, 175.0, 30, "M", "moderate", 68.0);

        // When
        boolean result = userHealthDataDao.addHealthData(healthData);

        // Then
        assertThat(result).isTrue();
        assertThat(healthData.getId()).isGreaterThan(0);
    }

    @Test
    void testAddHealthDataFailure() {
        // Given - 添加不存在用户的健康数据（外键约束失败）
        UserHealthData healthData = createTestHealthData(0, 999, 70.0, 175.0, 30, "M", "moderate", 68.0);

        // When & Then
        assertThrows(RuntimeException.class, () -> userHealthDataDao.addHealthData(healthData));
    }

    @Test
    void testGetHealthDataByIdSuccess() {
        // Given
        setupTestData();
        int healthDataId = 1;

        // When
        UserHealthData result = userHealthDataDao.getHealthDataById(healthDataId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(healthDataId);
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getWeight()).isEqualTo(70.0);
    }

    @Test
    void testGetHealthDataByIdNotFound() {
        // Given
        int healthDataId = 999;

        // When
        UserHealthData result = userHealthDataDao.getHealthDataById(healthDataId);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetLatestHealthDataByUserIdSuccess() {
        // Given
        setupTestData();
        int userId = 1;

        // When
        UserHealthData result = userHealthDataDao.getLatestHealthDataByUserId(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
        // 应该是用户1的健康数据之一
        assertThat(result.getWeight()).isIn(70.0, 72.0);
    }

    @Test
    void testGetLatestHealthDataByUserIdNotFound() {
        // Given
        int userId = 999;

        // When
        UserHealthData result = userHealthDataDao.getLatestHealthDataByUserId(userId);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetHealthDataByUserId() {
        // Given
        setupTestData();
        int userId = 1;

        // When
        List<UserHealthData> result = userHealthDataDao.getHealthDataByUserId(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        assertThat(result.get(1).getUserId()).isEqualTo(userId);
    }

    @Test
    void testUpdateHealthDataSuccess() {
        // Given
        setupTestData();
        UserHealthData healthData = userHealthDataDao.getHealthDataById(1);
        healthData.setWeight(75.0);

        // When
        boolean result = userHealthDataDao.updateHealthData(healthData);

        // Then
        assertThat(result).isTrue();
        UserHealthData updated = userHealthDataDao.getHealthDataById(1);
        assertThat(updated.getWeight()).isEqualTo(75.0);
    }

    @Test
    void testUpdateHealthDataFailure() {
        // Given
        UserHealthData healthData = createTestHealthData(999, 1, 75.0, 175.0, 30, "M", "moderate", 68.0);

        // When
        boolean result = userHealthDataDao.updateHealthData(healthData);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testDeleteHealthDataSuccess() {
        // Given
        setupTestData();

        // When
        boolean result = userHealthDataDao.deleteHealthData(1);

        // Then
        assertThat(result).isTrue();
        assertThat(userHealthDataDao.getHealthDataById(1)).isNull();
    }

    @Test
    void testDeleteHealthDataFailure() {
        // Given
        int healthDataId = 999;

        // When
        boolean result = userHealthDataDao.deleteHealthData(healthDataId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testHasHealthDataTrue() {
        // Given
        setupTestData();
        int userId = 1;

        // When
        boolean result = userHealthDataDao.hasHealthData(userId);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testHasHealthDataFalse() {
        // Given
        int userId = 999;

        // When
        boolean result = userHealthDataDao.hasHealthData(userId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testGetBMIHistory() {
        // Given
        setupTestData();
        int userId = 1;

        // When
        List<String> result = userHealthDataDao.getBMIHistory(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).contains("BMI:");
        assertThat(result.get(0)).contains("正常");
    }
}
