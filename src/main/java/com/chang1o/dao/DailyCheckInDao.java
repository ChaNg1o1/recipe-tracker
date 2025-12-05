package com.chang1o.dao;

import com.chang1o.model.DailyCheckIn;
import com.chang1o.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DailyCheckInDao {

    public boolean addCheckIn(DailyCheckIn checkIn) {
        String sql = "INSERT INTO daily_check_in (user_id, check_in_date, mood, sleep_hours, water_intake, exercise_minutes, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, checkIn.getUserId());
            pstmt.setDate(2, Date.valueOf(checkIn.getCheckInDate()));
            pstmt.setString(3, checkIn.getMood());
            pstmt.setDouble(4, checkIn.getSleepHours());
            pstmt.setInt(5, checkIn.getWaterIntake());
            pstmt.setInt(6, checkIn.getExerciseMinutes());
            pstmt.setString(7, checkIn.getNotes());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        checkIn.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("添加每日打卡记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public DailyCheckIn getCheckInById(int id) {
        String sql = "SELECT * FROM daily_check_in WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCheckInFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("根据ID获取打卡记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public DailyCheckIn getCheckInByUserIdAndDate(int userId, LocalDate date) {
        String sql = "SELECT * FROM daily_check_in WHERE user_id = ? AND check_in_date = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractCheckInFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("获取用户指定日期的打卡记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public List<DailyCheckIn> getCheckInsByUserId(int userId) {
        List<DailyCheckIn> checkInList = new ArrayList<>();
        String sql = "SELECT * FROM daily_check_in WHERE user_id = ? ORDER BY check_in_date DESC";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    checkInList.add(extractCheckInFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("获取用户打卡记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return checkInList;
    }

    public List<DailyCheckIn> getCheckInsByDateRange(int userId, LocalDate startDate, LocalDate endDate) {
        List<DailyCheckIn> checkInList = new ArrayList<>();
        String sql = "SELECT * FROM daily_check_in WHERE user_id = ? AND check_in_date BETWEEN ? AND ? ORDER BY check_in_date DESC";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    checkInList.add(extractCheckInFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("获取用户指定日期范围的打卡记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return checkInList;
    }

    public List<DailyCheckIn> getRecentCheckIns(int userId, int days) {
        List<DailyCheckIn> checkInList = new ArrayList<>();
        LocalDate startDate = LocalDate.now().minusDays(days - 1);

        String sql = "SELECT * FROM daily_check_in WHERE user_id = ? AND check_in_date >= ? ORDER BY check_in_date DESC";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(startDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    checkInList.add(extractCheckInFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("获取用户最近打卡记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return checkInList;
    }

    public boolean updateCheckIn(DailyCheckIn checkIn) {
        String sql = "UPDATE daily_check_in SET mood = ?, sleep_hours = ?, water_intake = ?, exercise_minutes = ?, notes = ? WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, checkIn.getMood());
            pstmt.setDouble(2, checkIn.getSleepHours());
            pstmt.setInt(3, checkIn.getWaterIntake());
            pstmt.setInt(4, checkIn.getExerciseMinutes());
            pstmt.setString(5, checkIn.getNotes());
            pstmt.setInt(6, checkIn.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("更新打卡记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteCheckIn(int id) {
        String sql = "DELETE FROM daily_check_in WHERE id = ?";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("删除打卡记录时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public int getConsecutiveCheckInDays(int userId) {
        String sql = "SELECT check_in_date FROM daily_check_in WHERE user_id = ? ORDER BY check_in_date DESC";

        try (Connection conn = DBUtil.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                List<LocalDate> checkInDates = new ArrayList<>();
                while (rs.next()) {
                    checkInDates.add(rs.getDate("check_in_date").toLocalDate());
                }

                return calculateConsecutiveDays(checkInDates);
            }
        } catch (SQLException e) {
            System.err.println("获取连续打卡天数时发生错误: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    private int calculateConsecutiveDays(List<LocalDate> dates) {
        if (dates.isEmpty()) return 0;

        int consecutiveDays = 0;
        LocalDate today = LocalDate.now();

        for (int i = 0; i < dates.size(); i++) {
            LocalDate expectedDate = today.minusDays(i);
            if (dates.get(i).equals(expectedDate)) {
                consecutiveDays++;
            } else {
                break;
            }
        }

        return consecutiveDays;
    }

    public boolean hasCheckedInToday(int userId) {
        LocalDate today = LocalDate.now();
        return getCheckInByUserIdAndDate(userId, today) != null;
    }

    public HealthStatistics getHealthStatistics(int userId, int days) {
        List<DailyCheckIn> recentCheckIns = getRecentCheckIns(userId, days);

        if (recentCheckIns.isEmpty()) {
            return new HealthStatistics(0, 0, 0, 0, 0, 0);
        }

        int totalScore = 0;
        double totalSleep = 0;
        int totalWater = 0;
        int totalExercise = 0;
        int moodScoreTotal = 0;

        for (DailyCheckIn checkIn : recentCheckIns) {
            totalScore += checkIn.getHealthScore();
            totalSleep += checkIn.getSleepHours();
            totalWater += checkIn.getWaterIntake();
            totalExercise += checkIn.getExerciseMinutes();
            moodScoreTotal += checkIn.getMoodScore();
        }

        int count = recentCheckIns.size();
        return new HealthStatistics(
            totalScore / count,
            totalSleep / count,
            totalWater / count,
            totalExercise / count,
            (double) moodScoreTotal / count,
            getConsecutiveCheckInDays(userId)
        );
    }

    private DailyCheckIn extractCheckInFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        LocalDate checkInDate = rs.getDate("check_in_date").toLocalDate();
        String mood = rs.getString("mood");
        double sleepHours = rs.getDouble("sleep_hours");
        int waterIntake = rs.getInt("water_intake");
        int exerciseMinutes = rs.getInt("exercise_minutes");
        String notes = rs.getString("notes");

        Timestamp createdAtTs = rs.getTimestamp("created_at");
        LocalDateTime createdAt = createdAtTs != null ? createdAtTs.toLocalDateTime() : null;

        return new DailyCheckIn(id, userId, checkInDate, mood, sleepHours, waterIntake, exerciseMinutes, notes, createdAt);
    }

    public static class HealthStatistics {
        private double avgHealthScore;
        private double avgSleepHours;
        private double avgWaterIntake;
        private double avgExerciseMinutes;
        private double avgMoodScore;
        private int consecutiveDays;

        public HealthStatistics(double avgHealthScore, double avgSleepHours, double avgWaterIntake,
                              double avgExerciseMinutes, double avgMoodScore, int consecutiveDays) {
            this.avgHealthScore = avgHealthScore;
            this.avgSleepHours = avgSleepHours;
            this.avgWaterIntake = avgWaterIntake;
            this.avgExerciseMinutes = avgExerciseMinutes;
            this.avgMoodScore = avgMoodScore;
            this.consecutiveDays = consecutiveDays;
        }

        public double getAvgHealthScore() { return avgHealthScore; }
        public double getAvgSleepHours() { return avgSleepHours; }
        public double getAvgWaterIntake() { return avgWaterIntake; }
        public double getAvgExerciseMinutes() { return avgExerciseMinutes; }
        public double getAvgMoodScore() { return avgMoodScore; }
        public int getConsecutiveDays() { return consecutiveDays; }

        @Override
        public String toString() {
            return String.format("健康统计 - 平均评分: %.1f/100, 平均睡眠: %.1f小时, 平均饮水: %.0fml, 平均运动: %.0f分钟, 连续打卡: %d天",
                avgHealthScore, avgSleepHours, avgWaterIntake, avgExerciseMinutes, consecutiveDays);
        }
    }

}