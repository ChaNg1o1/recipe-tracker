package com.chang1o.dao;

import com.chang1o.model.UserHealthData;
import com.chang1o.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserHealthDataDao {

    public boolean addHealthData(UserHealthData healthData) {
        String sql = "INSERT INTO user_health_data (user_id, weight, height, age, gender, activity_level, target_weight) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, healthData.getUserId());
            pstmt.setDouble(2, healthData.getWeight());
            pstmt.setDouble(3, healthData.getHeight());
            pstmt.setInt(4, healthData.getAge());
            pstmt.setString(5, healthData.getGender());
            pstmt.setString(6, healthData.getActivityLevel());
            pstmt.setDouble(7, healthData.getTargetWeight());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        healthData.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
        System.err.println("添加用户健康数据时发生错误: " + e.getMessage());
        throw new RuntimeException(e);
        }

        return false;
    }

    public UserHealthData getHealthDataById(int id) {
        String sql = "SELECT * FROM user_health_data WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractHealthDataFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("根据ID获取用户健康数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public UserHealthData getLatestHealthDataByUserId(int userId) {
        String sql = "SELECT * FROM user_health_data WHERE user_id = ? ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractHealthDataFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("获取用户最新健康数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<UserHealthData> getHealthDataByUserId(int userId) {
        List<UserHealthData> healthDataList = new ArrayList<>();
        String sql = "SELECT * FROM user_health_data WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    healthDataList.add(extractHealthDataFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("获取用户健康数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return healthDataList;
    }

    public boolean updateHealthData(UserHealthData healthData) {
        String sql = "UPDATE user_health_data SET weight = ?, height = ?, age = ?, gender = ?, activity_level = ?, target_weight = ? WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, healthData.getWeight());
            pstmt.setDouble(2, healthData.getHeight());
            pstmt.setInt(3, healthData.getAge());
            pstmt.setString(4, healthData.getGender());
            pstmt.setString(5, healthData.getActivityLevel());
            pstmt.setDouble(6, healthData.getTargetWeight());
            pstmt.setInt(7, healthData.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("更新用户健康数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteHealthData(int id) {
        String sql = "DELETE FROM user_health_data WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("删除用户健康数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasHealthData(int userId) {
        String sql = "SELECT COUNT(*) FROM user_health_data WHERE user_id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("检查用户健康数据时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getBMIHistory(int userId) {
        List<String> bmiHistory = new ArrayList<>();
        String sql = "SELECT created_at, weight, height FROM user_health_data WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    double weight = rs.getDouble("weight");
                    double height = rs.getDouble("height");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

                    if (height > 0) {
                        double heightInMeters = height / 100.0;
                        double bmi = weight / (heightInMeters * heightInMeters);
                        String bmiCategory = getBMICategory(bmi);

                        String record = String.format("%s - BMI: %.1f (%s)",
                            createdAt.toLocalDate(), bmi, bmiCategory);
                        bmiHistory.add(record);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("获取用户BMI历史时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return bmiHistory;
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "偏瘦";
        if (bmi < 24) return "正常";
        if (bmi < 28) return "超重";
        return "肥胖";
    }

    private UserHealthData extractHealthDataFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        double weight = rs.getDouble("weight");
        double height = rs.getDouble("height");
        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String activityLevel = rs.getString("activity_level");
        double targetWeight = rs.getDouble("target_weight");

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");

        LocalDateTime createdAt = createdAtTs != null ? createdAtTs.toLocalDateTime() : null;
        LocalDateTime updatedAt = updatedAtTs != null ? updatedAtTs.toLocalDateTime() : null;

        return new UserHealthData(id, userId, weight, height, age, gender, activityLevel, targetWeight, createdAt, updatedAt);
    }
}