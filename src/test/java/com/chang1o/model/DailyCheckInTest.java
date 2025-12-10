package com.chang1o.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@DisplayName("DailyCheckIn模型测试")
class DailyCheckInTest {

    private DailyCheckIn dailyCheckIn;
    private LocalDate today;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        dailyCheckIn = new DailyCheckIn();
        today = LocalDate.now();
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        DailyCheckIn defaultCheckIn = new DailyCheckIn();
        assertThat(defaultCheckIn).isNotNull();
        assertThat(defaultCheckIn.getId()).isEqualTo(0);
        assertThat(defaultCheckIn.getUserId()).isEqualTo(0);
        assertThat(defaultCheckIn.getCheckInDate()).isNull();
        assertThat(defaultCheckIn.getMood()).isNull();
        assertThat(defaultCheckIn.getSleepHours()).isEqualTo(0.0);
        assertThat(defaultCheckIn.getWaterIntake()).isEqualTo(0);
        assertThat(defaultCheckIn.getExerciseMinutes()).isEqualTo(0);
        assertThat(defaultCheckIn.getNotes()).isNull();
        assertThat(defaultCheckIn.getCreatedAt()).isNull();
    }

    @Test
    @DisplayName("测试带参数的构造函数")
    void testConstructorWithParameters() {
        int userId = 123;
        String mood = "good";
        double sleepHours = 8.5;
        int waterIntake = 2000;
        int exerciseMinutes = 30;
        String notes = "Feeling great today";

        DailyCheckIn checkInWithParams = new DailyCheckIn(userId, today, mood, sleepHours, waterIntake, exerciseMinutes, notes);

        assertThat(checkInWithParams).isNotNull();
        assertThat(checkInWithParams.getUserId()).isEqualTo(userId);
        assertThat(checkInWithParams.getCheckInDate()).isEqualTo(today);
        assertThat(checkInWithParams.getMood()).isEqualTo(mood);
        assertThat(checkInWithParams.getSleepHours()).isEqualTo(sleepHours);
        assertThat(checkInWithParams.getWaterIntake()).isEqualTo(waterIntake);
        assertThat(checkInWithParams.getExerciseMinutes()).isEqualTo(exerciseMinutes);
        assertThat(checkInWithParams.getNotes()).isEqualTo(notes);
        assertThat(checkInWithParams.getId()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试完整构造函数")
    void testConstructorWithAllParameters() {
        int id = 1;
        int userId = 123;
        String mood = "great";
        double sleepHours = 9.0;
        int waterIntake = 2500;
        int exerciseMinutes = 60;
        String notes = "Excellent day";

        DailyCheckIn fullCheckIn = new DailyCheckIn(id, userId, today, mood, sleepHours, waterIntake, exerciseMinutes, notes, now);

        assertThat(fullCheckIn).isNotNull();
        assertThat(fullCheckIn.getId()).isEqualTo(id);
        assertThat(fullCheckIn.getUserId()).isEqualTo(userId);
        assertThat(fullCheckIn.getCheckInDate()).isEqualTo(today);
        assertThat(fullCheckIn.getMood()).isEqualTo(mood);
        assertThat(fullCheckIn.getSleepHours()).isEqualTo(sleepHours);
        assertThat(fullCheckIn.getWaterIntake()).isEqualTo(waterIntake);
        assertThat(fullCheckIn.getExerciseMinutes()).isEqualTo(exerciseMinutes);
        assertThat(fullCheckIn.getNotes()).isEqualTo(notes);
        assertThat(fullCheckIn.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("测试set和get方法")
    void testSetAndGetMethods() {
        int id = 10;
        int userId = 100;
        String mood = "normal";
        double sleepHours = 7.5;
        int waterIntake = 1500;
        int exerciseMinutes = 45;
        String notes = "Regular day";

        dailyCheckIn.setId(id);
        dailyCheckIn.setUserId(userId);
        dailyCheckIn.setCheckInDate(today);
        dailyCheckIn.setMood(mood);
        dailyCheckIn.setSleepHours(sleepHours);
        dailyCheckIn.setWaterIntake(waterIntake);
        dailyCheckIn.setExerciseMinutes(exerciseMinutes);
        dailyCheckIn.setNotes(notes);
        dailyCheckIn.setCreatedAt(now);

        assertThat(dailyCheckIn.getId()).isEqualTo(id);
        assertThat(dailyCheckIn.getUserId()).isEqualTo(userId);
        assertThat(dailyCheckIn.getCheckInDate()).isEqualTo(today);
        assertThat(dailyCheckIn.getMood()).isEqualTo(mood);
        assertThat(dailyCheckIn.getSleepHours()).isEqualTo(sleepHours);
        assertThat(dailyCheckIn.getWaterIntake()).isEqualTo(waterIntake);
        assertThat(dailyCheckIn.getExerciseMinutes()).isEqualTo(exerciseMinutes);
        assertThat(dailyCheckIn.getNotes()).isEqualTo(notes);
        assertThat(dailyCheckIn.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("测试getMoodDescription方法")
    void testGetMoodDescription() {
        dailyCheckIn.setMood("great");
        assertThat(dailyCheckIn.getMoodDescription()).isEqualTo("非常好");

        dailyCheckIn.setMood("good");
        assertThat(dailyCheckIn.getMoodDescription()).isEqualTo("好");

        dailyCheckIn.setMood("normal");
        assertThat(dailyCheckIn.getMoodDescription()).isEqualTo("一般");

        dailyCheckIn.setMood("bad");
        assertThat(dailyCheckIn.getMoodDescription()).isEqualTo("差");

        dailyCheckIn.setMood("terrible");
        assertThat(dailyCheckIn.getMoodDescription()).isEqualTo("非常差");

        dailyCheckIn.setMood("unknown");
        assertThat(dailyCheckIn.getMoodDescription()).isEqualTo("未知");

        dailyCheckIn.setMood(null);
        assertThat(dailyCheckIn.getMoodDescription()).isEqualTo("未知");
    }

    @Test
    @DisplayName("测试getMoodScore方法")
    void testGetMoodScore() {
        dailyCheckIn.setMood("great");
        assertThat(dailyCheckIn.getMoodScore()).isEqualTo(5);

        dailyCheckIn.setMood("good");
        assertThat(dailyCheckIn.getMoodScore()).isEqualTo(4);

        dailyCheckIn.setMood("normal");
        assertThat(dailyCheckIn.getMoodScore()).isEqualTo(3);

        dailyCheckIn.setMood("bad");
        assertThat(dailyCheckIn.getMoodScore()).isEqualTo(2);

        dailyCheckIn.setMood("terrible");
        assertThat(dailyCheckIn.getMoodScore()).isEqualTo(1);

        dailyCheckIn.setMood("unknown");
        assertThat(dailyCheckIn.getMoodScore()).isEqualTo(3);

        // 测试null情况 - 先设置一个非null值再设为null
        dailyCheckIn.setMood("good");
        dailyCheckIn.setMood(null);
        assertThat(dailyCheckIn.getMoodScore()).isEqualTo(3);
    }

    @Test
    @DisplayName("测试getHealthScore方法")
    void testGetHealthScore() {
        // 测试完美健康分数
        dailyCheckIn.setMood("great");
        dailyCheckIn.setSleepHours(9.0);
        dailyCheckIn.setWaterIntake(2500);
        dailyCheckIn.setExerciseMinutes(90);
        int perfectScore = dailyCheckIn.getHealthScore();
        assertThat(perfectScore).isEqualTo(100);

        // 测试最低健康分数
        dailyCheckIn.setMood("terrible");
        dailyCheckIn.setSleepHours(2.0);
        dailyCheckIn.setWaterIntake(0);
        dailyCheckIn.setExerciseMinutes(0);
        int minScore = dailyCheckIn.getHealthScore();
        assertThat(minScore).isGreaterThan(0);
        assertThat(minScore).isLessThan(100);

        // 测试中等分数
        dailyCheckIn.setMood("normal");
        dailyCheckIn.setSleepHours(7.0);
        dailyCheckIn.setWaterIntake(1500);
        dailyCheckIn.setExerciseMinutes(30);
        int mediumScore = dailyCheckIn.getHealthScore();
        assertThat(mediumScore).isGreaterThan(minScore);
        assertThat(mediumScore).isLessThan(perfectScore);
    }

    @Test
    @DisplayName("测试健康分数计算的边界情况")
    void testHealthScoreBoundaryCases() {
        // 测试睡眠时间边界
        dailyCheckIn.setMood("good");
        dailyCheckIn.setWaterIntake(2000);
        dailyCheckIn.setExerciseMinutes(60);

        dailyCheckIn.setSleepHours(8.0);
        int score8Hours = dailyCheckIn.getHealthScore();

        dailyCheckIn.setSleepHours(7.0);
        int score7Hours = dailyCheckIn.getHealthScore();
        assertThat(score7Hours).isLessThan(score8Hours);

        dailyCheckIn.setSleepHours(4.0);
        int score4Hours = dailyCheckIn.getHealthScore();
        assertThat(score4Hours).isLessThan(score7Hours);

        // 测试饮水量边界
        dailyCheckIn.setSleepHours(8.0);
        dailyCheckIn.setExerciseMinutes(60);

        dailyCheckIn.setWaterIntake(2000);
        int score2000ml = dailyCheckIn.getHealthScore();

        dailyCheckIn.setWaterIntake(1000);
        int score1000ml = dailyCheckIn.getHealthScore();
        assertThat(score1000ml).isLessThan(score2000ml);

        // 测试运动时间边界
        dailyCheckIn.setWaterIntake(2000);

        dailyCheckIn.setExerciseMinutes(60);
        int score60Min = dailyCheckIn.getHealthScore();

        dailyCheckIn.setExerciseMinutes(15);
        int score15Min = dailyCheckIn.getHealthScore();
        assertThat(score15Min).isLessThan(score60Min);
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        dailyCheckIn.setId(1);
        dailyCheckIn.setUserId(100);
        dailyCheckIn.setCheckInDate(today);
        dailyCheckIn.setMood("good");
        dailyCheckIn.setSleepHours(8.0);
        dailyCheckIn.setWaterIntake(2000);
        dailyCheckIn.setExerciseMinutes(30);

        String result = dailyCheckIn.toString();

        assertThat(result).contains("DailyCheckIn{");
        assertThat(result).contains("id=1");
        assertThat(result).contains("userId=100");
        assertThat(result).contains("checkInDate=" + today);
        assertThat(result).contains("mood='good'");
        assertThat(result).contains("sleepHours=8.0");
        assertThat(result).contains("waterIntake=2000");
        assertThat(result).contains("exerciseMinutes=30");
        assertThat(result).contains("healthScore=");
    }

    @Test
    @DisplayName("测试toString方法包含null值")
    void testToStringWithNullValues() {
        // 先设置一个非null的心情值再设为null，避免NPE
        dailyCheckIn.setMood("good");
        dailyCheckIn.setMood(null);

        String result = dailyCheckIn.toString();

        assertThat(result).contains("DailyCheckIn{");
        assertThat(result).contains("id=0");
        assertThat(result).contains("userId=0");
        assertThat(result).contains("checkInDate=null");
        assertThat(result).contains("mood='null'");
        assertThat(result).contains("sleepHours=0.0");
        assertThat(result).contains("waterIntake=0");
        assertThat(result).contains("exerciseMinutes=0");
        assertThat(result).contains("healthScore=");
    }

    @Test
    @DisplayName("测试边界值")
    void testBoundaryValues() {
        dailyCheckIn.setId(Integer.MIN_VALUE);
        assertThat(dailyCheckIn.getId()).isEqualTo(Integer.MIN_VALUE);

        dailyCheckIn.setId(Integer.MAX_VALUE);
        assertThat(dailyCheckIn.getId()).isEqualTo(Integer.MAX_VALUE);

        dailyCheckIn.setUserId(Integer.MIN_VALUE);
        assertThat(dailyCheckIn.getUserId()).isEqualTo(Integer.MIN_VALUE);

        dailyCheckIn.setWaterIntake(Integer.MIN_VALUE);
        assertThat(dailyCheckIn.getWaterIntake()).isEqualTo(Integer.MIN_VALUE);

        dailyCheckIn.setExerciseMinutes(Integer.MAX_VALUE);
        assertThat(dailyCheckIn.getExerciseMinutes()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("测试数值字段的边界值")
    void testNumericFieldBoundaryValues() {
        dailyCheckIn.setSleepHours(Double.MIN_VALUE);
        assertThat(dailyCheckIn.getSleepHours()).isEqualTo(Double.MIN_VALUE);

        dailyCheckIn.setSleepHours(Double.MAX_VALUE);
        assertThat(dailyCheckIn.getSleepHours()).isEqualTo(Double.MAX_VALUE);

        dailyCheckIn.setSleepHours(0.0);
        assertThat(dailyCheckIn.getSleepHours()).isEqualTo(0.0);

        dailyCheckIn.setSleepHours(-1.0);
        assertThat(dailyCheckIn.getSleepHours()).isEqualTo(-1.0);
    }

    @Test
    @DisplayName("测试字符串字段的边界值")
    void testStringFieldBoundaryValues() {
        dailyCheckIn.setMood("");
        assertThat(dailyCheckIn.getMood()).isEmpty();

        String longMood = "a".repeat(100);
        dailyCheckIn.setMood(longMood);
        assertThat(dailyCheckIn.getMood()).isEqualTo(longMood);

        dailyCheckIn.setMood(null);
        assertThat(dailyCheckIn.getMood()).isNull();

        dailyCheckIn.setNotes("");
        assertThat(dailyCheckIn.getNotes()).isEmpty();

        String longNotes = "b".repeat(1000);
        dailyCheckIn.setNotes(longNotes);
        assertThat(dailyCheckIn.getNotes()).isEqualTo(longNotes);

        dailyCheckIn.setNotes(null);
        assertThat(dailyCheckIn.getNotes()).isNull();
    }

    @Test
    @DisplayName("测试日期时间字段")
    void testDateTimeFields() {
        LocalDate pastDate = today.minusDays(30);
        LocalDate futureDate = today.plusDays(30);
        LocalDateTime pastDateTime = now.minusHours(24);
        LocalDateTime futureDateTime = now.plusHours(24);

        dailyCheckIn.setCheckInDate(pastDate);
        assertThat(dailyCheckIn.getCheckInDate()).isEqualTo(pastDate);

        dailyCheckIn.setCheckInDate(futureDate);
        assertThat(dailyCheckIn.getCheckInDate()).isEqualTo(futureDate);

        dailyCheckIn.setCreatedAt(pastDateTime);
        assertThat(dailyCheckIn.getCreatedAt()).isEqualTo(pastDateTime);

        dailyCheckIn.setCreatedAt(futureDateTime);
        assertThat(dailyCheckIn.getCreatedAt()).isEqualTo(futureDateTime);

        dailyCheckIn.setCheckInDate(null);
        assertThat(dailyCheckIn.getCheckInDate()).isNull();

        dailyCheckIn.setCreatedAt(null);
        assertThat(dailyCheckIn.getCreatedAt()).isNull();
    }
}