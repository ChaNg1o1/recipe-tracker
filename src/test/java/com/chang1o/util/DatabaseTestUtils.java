package com.chang1o.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 数据库测试工具类 - 提供测试数据库操作的基础功能
 */
public class DatabaseTestUtils {
    private static DatabaseTestUtils instance;
    private static final String TEST_SCHEMA_FILE = "test-schema.sql";

    private DatabaseTestUtils() {
        // 设置测试数据库配置
        System.setProperty("DB_CONFIG", "test-database.properties");
        // 重置DBUtil实例以使用测试配置
        DBUtil.resetInstance();
    }

    public static DatabaseTestUtils getInstance() {
        if (instance == null) {
            instance = new DatabaseTestUtils();
        }
        return instance;
    }

    /**
     * 初始化测试数据库schema
     */
    public void initializeTestSchema() throws SQLException {
        try (Connection conn = getConnection()) {
            String schemaScript = loadSchemaScript();
            executeScript(conn, schemaScript);
        } catch (IOException e) {
            throw new SQLException("加载测试schema脚本失败", e);
        }
    }

    /**
     * 清空所有表数据
     */
    public void truncateAllTables() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // 禁用外键约束检查
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            
            // 清空所有表
            String[] tables = {
                "daily_check_in", "user_health_data", "recipe_ingredients", 
                "pantry", "recipes", "ingredients", "categories", "users"
            };
            
            for (String table : tables) {
                stmt.execute("TRUNCATE TABLE " + table);
            }
            
            // 重新启用外键约束检查
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    /**
     * 插入测试数据到指定表
     */
    public void insertTestData(String tableName, Map<String, Object> data) throws SQLException {
        if (data.isEmpty()) {
            return;
        }

        StringJoiner columns = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        
        for (String column : data.keySet()) {
            columns.add(column);
            placeholders.add("?");
        }

        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", 
                                 tableName, columns.toString(), placeholders.toString());

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int index = 1;
            for (Object value : data.values()) {
                stmt.setObject(index++, value);
            }
            
            stmt.executeUpdate();
        }
    }

    /**
     * 统计表中记录数
     */
    public int countRecords(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    /**
     * 检查表是否存在
     */
    public boolean tableExists(String tableName) throws SQLException {
        try (Connection conn = getConnection();
             ResultSet rs = conn.getMetaData().getTables(null, null, tableName.toUpperCase(), null)) {
            return rs.next();
        }
    }

    /**
     * 执行SQL查询并返回结果集
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        
        return stmt.executeQuery();
    }

    /**
     * 执行SQL更新语句
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            
            return stmt.executeUpdate();
        }
    }

    /**
     * 获取数据库连接
     */
    private Connection getConnection() throws SQLException {
        return DBUtil.getInstance().getConnection();
    }

    /**
     * 加载schema脚本
     */
    private String loadSchemaScript() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(TEST_SCHEMA_FILE);
        if (inputStream == null) {
            throw new IOException("找不到测试schema文件: " + TEST_SCHEMA_FILE);
        }

        StringBuilder script = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 跳过注释行
                if (!line.trim().startsWith("--") && !line.trim().isEmpty()) {
                    script.append(line).append("\n");
                }
            }
        }
        
        return script.toString();
    }

    /**
     * 执行SQL脚本
     */
    private void executeScript(Connection conn, String script) throws SQLException {
        String[] statements = script.split(";");
        
        try (Statement stmt = conn.createStatement()) {
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }

    /**
     * 重置实例（测试专用）
     */
    public static void resetInstance() {
        instance = null;
    }
}