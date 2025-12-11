package com.chang1o.dao;

import com.chang1o.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserDao单元测试
 * 使用真实的数据库连接进行测试，确保数据库状态正确隔离
 */
class UserDaoTest {

    private UserDao userDao;

    @BeforeEach
    void setUp() {
        // 设置测试数据库配置
        System.setProperty("DB_CONFIG", "test-database.properties");
        // 重置DBUtil实例以强制重新加载测试配置
        com.chang1o.util.DBUtil.resetInstance();
        userDao = new UserDao();
        cleanDatabase();
        initializeTestSchema();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void initializeTestSchema() {
        try (var conn = com.chang1o.util.DBUtil.getInstance().getConnection();
             var stmt = conn.createStatement()) {
            
            // 检查表是否已存在
            try {
                stmt.executeQuery("SELECT 1 FROM users LIMIT 1");
                // 如果查询成功，表已存在，无需重新创建
                return;
            } catch (Exception e) {
                // 表不存在，需要创建
            }
            
            // 创建用户表
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL
                )
            """);
            
            // 创建其他必要的表
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS categories (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL UNIQUE
                )
            """);
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS ingredients (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL UNIQUE
                )
            """);
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS recipes (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(255) NOT NULL,
                    instructions TEXT,
                    category_id INT,
                    user_id INT NOT NULL
                )
            """);
            
        } catch (Exception e) {
            System.err.println("初始化测试数据库schema失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cleanDatabase() {
        try (var conn = com.chang1o.util.DBUtil.getInstance().getConnection();
             var stmt = conn.createStatement()) {
            
            // 简单清理用户表数据
            try {
                stmt.execute("DELETE FROM users");
            } catch (Exception e) {
                // 表可能不存在，忽略错误
            }
            
            // 清理其他表
            try {
                stmt.execute("DELETE FROM recipes");
            } catch (Exception e) {
                // 表可能不存在，忽略错误
            }
            
            try {
                stmt.execute("DELETE FROM categories");
            } catch (Exception e) {
                // 表可能不存在，忽略错误
            }
            
            try {
                stmt.execute("DELETE FROM ingredients");
            } catch (Exception e) {
                // 表可能不存在，忽略错误
            }
            
        } catch (Exception e) {
            System.err.println("清理测试数据库失败: " + e.getMessage());
        }
    }



    private void setupTestData() {
        // 创建测试用户数据
        User user1 = createTestUser(0, "testuser", "password123");
        User user2 = createTestUser(0, "user1", "pass1");
        User user3 = createTestUser(0, "user2", "pass2");

        userDao.addUser(user1);
        userDao.addUser(user2);
        userDao.addUser(user3);
    }

    private User createTestUser(int id, String username, String password) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    @Test
    void testAddUserSuccess() {
        // Given
        User user = createTestUser(0, "testuser", "password123");
        // Mock DBUtil.getInstance().getConnection() 返回mockConnection
        // Mock mockConnection.prepareStatement() 返回mockPreparedStatement
        // Mock mockPreparedStatement.executeUpdate() 返回1

        // 这里需要更复杂的Mock设置，实际项目中可能需要使用Testcontainers或内存数据库

        // When
        boolean result = userDao.addUser(user);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testAddUserFailure() {
        // Given - 先添加一个用户，再添加同名用户，测试UNIQUE约束失败
        User user1 = createTestUser(0, "testuser", "password123");
        User user2 = createTestUser(0, "testuser", "differentpassword");

        // 添加第一个用户，应该成功
        boolean result1 = userDao.addUser(user1);
        assertThat(result1).isTrue();

        // 添加同名用户，应该失败
        boolean result2 = userDao.addUser(user2);

        // Then
        assertThat(result2).isFalse();
    }

    @Test
    void testGetUserByUsernameSuccess() {
        // Given
        setupTestData();
        String username = "testuser";
        User expectedUser = createTestUser(1, username, "password123");

        // When
        User result = userDao.getUserByUsername(username);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getPassword()).isEqualTo("password123");
    }

    @Test
    void testGetUserByUsernameNotFound() {
        // Given
        String username = "nonexistent";

        // When
        User result = userDao.getUserByUsername(username);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetUserByIdSuccess() {
        // Given
        setupTestData();
        // Get the actual user first to know the ID
        User testUser = userDao.getUserByUsername("testuser");
        assertThat(testUser).isNotNull();
        int userId = testUser.getId();

        // When
        User result = userDao.getUserById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void testGetUserByIdNotFound() {
        // Given
        int userId = 999;

        // When
        User result = userDao.getUserById(userId);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testAuthenticateSuccess() {
        // Given
        setupTestData();
        String username = "testuser";
        String password = "password123";

        // When
        User result = userDao.authenticate(username, password);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
    }

    @Test
    void testAuthenticateWrongPassword() {
        // Given
        setupTestData();
        String username = "testuser";
        String wrongPassword = "wrongpassword";

        // When
        User result = userDao.authenticate(username, wrongPassword);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testAuthenticateUserNotFound() {
        // Given
        String username = "nonexistent";
        String password = "password123";

        // When
        User result = userDao.authenticate(username, password);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetAllUsers() {
        // Given
        setupTestData();

        // When
        List<User> result = userDao.getAllUsers();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        assertThat(result.get(1).getUsername()).isEqualTo("user1");
        assertThat(result.get(2).getUsername()).isEqualTo("user2");
    }

    @Test
    void testUpdateUserSuccess() {
        // Given
        setupTestData();
        User user = userDao.getUserByUsername("testuser");
        assertThat(user).isNotNull();
        int userId = user.getId();
        user.setUsername("updateduser");

        // When
        boolean result = userDao.updateUser(user);

        // Then
        assertThat(result).isTrue();
        User updated = userDao.getUserById(userId);
        assertThat(updated.getUsername()).isEqualTo("updateduser");
    }

    @Test
    void testUpdateUserFailure() {
        // Given
        User user = createTestUser(999, "updateduser", "newpassword");

        // When
        boolean result = userDao.updateUser(user);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testDeleteUserSuccess() {
        // Given
        setupTestData();
        User user = userDao.getUserByUsername("testuser");
        assertThat(user).isNotNull();
        int userId = user.getId();

        // When
        boolean result = userDao.deleteUser(userId);

        // Then
        assertThat(result).isTrue();
        assertThat(userDao.getUserById(userId)).isNull();
    }

    @Test
    void testDeleteUserFailure() {
        // Given
        int userId = 999;

        // When
        boolean result = userDao.deleteUser(userId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testIsUsernameExistsTrue() {
        // Given
        setupTestData();
        String username = "testuser";

        // When
        boolean result = userDao.isUsernameExists(username);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testIsUsernameExistsFalse() {
        // Given
        String username = "nonexistent";

        // When
        boolean result = userDao.isUsernameExists(username);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testCountUsers() {
        // Given
        setupTestData();

        // When
        int result = userDao.countUsers();

        // Then
        assertThat(result).isEqualTo(3);
    }
}
