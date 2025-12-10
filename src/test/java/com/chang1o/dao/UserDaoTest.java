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
        initializeTestSchema();
        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void initializeTestSchema() {
        try (var conn = com.chang1o.util.DBUtil.getInstance().getConnection();
             var stmt = conn.createStatement()) {
            // 直接初始化测试schema
            try (var reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("test-schema.sql")))) {
                String line;
                StringBuilder currentStatement = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    // 忽略注释行
                    if (line.trim().startsWith("--")) {
                        continue;
                    }

                    currentStatement.append(line).append(" ");
                    if (line.trim().endsWith(";")) {
                        // 执行完整的SQL语句，忽略表已存在的错误
                        String sql = currentStatement.toString().trim();
                        try {
                            stmt.execute(sql);
                        } catch (Exception e) {
                            // 忽略表已存在的错误，但保留其他错误
                            if (!e.getMessage().contains("already exists")) {
                                throw e;
                            }
                        }
                        currentStatement.setLength(0);
                    }
                }
                // 执行最后一个可能未以分号结束的语句
                if (currentStatement.length() > 0) {
                    stmt.execute(currentStatement.toString().trim());
                }
            }
        } catch (Exception e) {
            System.err.println("初始化测试数据库schema失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cleanDatabase() {
        try (var conn = com.chang1o.util.DBUtil.getInstance().getConnection();
             var stmt = conn.createStatement()) {
            // H2数据库清空方式
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            // 清理所有相关的表以确保完全隔离（按照依赖关系倒序）
            stmt.execute("TRUNCATE TABLE recipe_ingredients");
            stmt.execute("TRUNCATE TABLE pantry");
            stmt.execute("TRUNCATE TABLE daily_check_in");
            stmt.execute("TRUNCATE TABLE user_health_data");
            stmt.execute("TRUNCATE TABLE recipes");
            stmt.execute("TRUNCATE TABLE ingredients");
            stmt.execute("TRUNCATE TABLE categories");
            stmt.execute("TRUNCATE TABLE users");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            try (var conn = com.chang1o.util.DBUtil.getInstance().getConnection();
                 var stmt = conn.createStatement()) {
                // 如果表结构清空失败，使用DROP ALL OBJECTS作为备选方案
                stmt.execute("DROP ALL OBJECTS");
            } catch (Exception ex) {
                System.err.println("清理测试数据库失败: " + ex.getMessage());
            }
        }
    }

    private void initializeSchemaIfNeeded(java.sql.Connection conn) {
        try (var stmt = conn.createStatement()) {
            // 检查users表是否存在
            var resultSet = stmt.executeQuery("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN ('USERS', 'users')");
            boolean tablesExist = false;
            if (resultSet.next()) {
                tablesExist = resultSet.getInt(1) > 0;
            }

            if (!tablesExist) {
                // 加载并执行测试schema
                try (var reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(
                            getClass().getClassLoader().getResourceAsStream("test-schema.sql")))) {
                    String line;
                    StringBuilder currentStatement = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        // 忽略注释行
                        if (line.trim().startsWith("--")) {
                            continue;
                        }

                        currentStatement.append(line).append(" ");
                        if (line.trim().endsWith(";")) {
                            // 执行完整的SQL语句
                            stmt.execute(currentStatement.toString().trim());
                            currentStatement.setLength(0);
                        }
                    }
                    // 执行最后一个可能未以分号结束的语句
                    if (currentStatement.length() > 0) {
                        stmt.execute(currentStatement.toString().trim());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("初始化测试数据库schema失败: " + e.getMessage());
            e.printStackTrace();
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
        int userId = 1;

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
        User user = userDao.getUserById(1);
        user.setUsername("updateduser");

        // When
        boolean result = userDao.updateUser(user);

        // Then
        assertThat(result).isTrue();
        User updated = userDao.getUserById(1);
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

        // When
        boolean result = userDao.deleteUser(1);

        // Then
        assertThat(result).isTrue();
        assertThat(userDao.getUserById(1)).isNull();
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
