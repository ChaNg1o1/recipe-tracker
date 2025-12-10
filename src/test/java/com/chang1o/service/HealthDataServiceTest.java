package com.chang1o.service;

import com.chang1o.dao.UserHealthDataDao;
import com.chang1o.dao.DailyCheckInDao;
import com.chang1o.model.UserHealthData;
import com.chang1o.model.DailyCheckIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthDataServiceTest {

    @Mock
    private UserHealthDataDao healthDataDao;

    @Mock
    private DailyCheckInDao dailyCheckInDao;

    private HealthDataService healthDataService;

    @BeforeEach
    void setUp() {
        healthDataService = new HealthDataService();
        injectMocks();
    }

    private void injectMocks() {
        try {
            java.lang.reflect.Field field = HealthDataService.class.getDeclaredField("healthDataDao");
            field.setAccessible(true);
            field.set(healthDataService, healthDataDao);
            
            field = HealthDataService.class.getDeclaredField("dailyCheckInDao");
            field.setAccessible(true);
            field.set(healthDataService, dailyCheckInDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UserHealthData createHealthData(int userId, double weight, double height, int age,
                                         String gender, String activityLevel, double targetWeight) {
        UserHealthData data = new UserHealthData();
        data.setUserId(userId);
        data.setWeight(weight);
        data.setHeight(height);
        data.setAge(age);
        data.setGender(gender);
        data.setActivityLevel(activityLevel);
        data.setTargetWeight(targetWeight);
        return data;
    }

    private DailyCheckIn createCheckIn(int userId, LocalDate date, String mood, 
                                    double sleepHours, int waterIntake, int exerciseMinutes, String notes) {
        DailyCheckIn checkIn = new DailyCheckIn();
        checkIn.setUserId(userId);
        checkIn.setCheckInDate(date);
        checkIn.setMood(mood);
        checkIn.setSleepHours(sleepHours);
        checkIn.setWaterIntake(waterIntake);
        checkIn.setExerciseMinutes(exerciseMinutes);
        checkIn.setNotes(notes);
        return checkIn;
    }

    @Test
    void testSaveHealthDataSuccessNew() {
        // Given
        int userId = 1;
        double weight = 70.0;
        double height = 170.0;
        int age = 25;
        String gender = "M";
        String activityLevel = "moderate";
        double targetWeight = 65.0;

        when(healthDataDao.getLatestHealthDataByUserId(userId)).thenReturn(null);
        when(healthDataDao.addHealthData(any(UserHealthData.class))).thenReturn(true);

        // When
        HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
            userId, weight, height, age, gender, activityLevel, targetWeight);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getHealthData()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("健康数据保存成功！");
        verify(healthDataDao).getLatestHealthDataByUserId(userId);
        verify(healthDataDao).addHealthData(any(UserHealthData.class));
        verify(healthDataDao, never()).updateHealthData(any(UserHealthData.class));
    }

    @Test
    void testSaveHealthDataSuccessUpdate() {
        // Given
        int userId = 1;
        double weight = 75.0;
        double height = 170.0;
        int age = 25;
        String gender = "M";
        String activityLevel = "moderate";
        double targetWeight = 65.0;

        UserHealthData existingData = createHealthData(userId, 70.0, 170.0, 25, "M", "moderate", 65.0);
        existingData.setId(1);
        
        when(healthDataDao.getLatestHealthDataByUserId(userId)).thenReturn(existingData);
        when(healthDataDao.updateHealthData(any(UserHealthData.class))).thenReturn(true);

        // When
        HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
            userId, weight, height, age, gender, activityLevel, targetWeight);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getHealthData()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("健康数据保存成功！");
        verify(healthDataDao).getLatestHealthDataByUserId(userId);
        verify(healthDataDao, never()).addHealthData(any(UserHealthData.class));
        verify(healthDataDao).updateHealthData(any(UserHealthData.class));
    }

    @Test
    void testSaveHealthDataInvalidWeight() {
        // Given
        int userId = 1;
        double weight = 0; // 无效体重
        double height = 170.0;
        int age = 25;
        String gender = "M";
        String activityLevel = "moderate";
        double targetWeight = 65.0;

        // When
        HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
            userId, weight, height, age, gender, activityLevel, targetWeight);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getHealthData()).isNull();
        assertThat(result.getMessage()).isEqualTo("体重必须在0-300kg之间");
        verify(healthDataDao, never()).getLatestHealthDataByUserId(anyInt());
        verify(healthDataDao, never()).addHealthData(any(UserHealthData.class));
        verify(healthDataDao, never()).updateHealthData(any(UserHealthData.class));
    }

    @Test
    void testSaveHealthDataInvalidHeight() {
        // Given
        int userId = 1;
        double weight = 70.0;
        double height = 0; // 无效身高
        int age = 25;
        String gender = "M";
        String activityLevel = "moderate";
        double targetWeight = 65.0;

        // When
        HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
            userId, weight, height, age, gender, activityLevel, targetWeight);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getHealthData()).isNull();
        assertThat(result.getMessage()).isEqualTo("身高必须在0-250cm之间");
        verify(healthDataDao, never()).getLatestHealthDataByUserId(anyInt());
        verify(healthDataDao, never()).addHealthData(any(UserHealthData.class));
        verify(healthDataDao, never()).updateHealthData(any(UserHealthData.class));
    }

    @Test
    void testSaveHealthDataInvalidAge() {
        // Given
        int userId = 1;
        double weight = 70.0;
        double height = 170.0;
        int age = 0; // 无效年龄
        String gender = "M";
        String activityLevel = "moderate";
        double targetWeight = 65.0;

        // When
        HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
            userId, weight, height, age, gender, activityLevel, targetWeight);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getHealthData()).isNull();
        assertThat(result.getMessage()).isEqualTo("年龄必须在0-150岁之间");
        verify(healthDataDao, never()).getLatestHealthDataByUserId(anyInt());
        verify(healthDataDao, never()).addHealthData(any(UserHealthData.class));
        verify(healthDataDao, never()).updateHealthData(any(UserHealthData.class));
    }

    @Test
    void testSaveHealthDataInvalidGender() {
        // Given
        int userId = 1;
        double weight = 70.0;
        double height = 170.0;
        int age = 25;
        String gender = "X"; // 无效性别
        String activityLevel = "moderate";
        double targetWeight = 65.0;

        // When
        HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
            userId, weight, height, age, gender, activityLevel, targetWeight);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getHealthData()).isNull();
        assertThat(result.getMessage()).isEqualTo("性别必须是M（男）或F（女）");
        verify(healthDataDao, never()).getLatestHealthDataByUserId(anyInt());
        verify(healthDataDao, never()).addHealthData(any(UserHealthData.class));
        verify(healthDataDao, never()).updateHealthData(any(UserHealthData.class));
    }

    @Test
    void testSaveHealthDataInvalidActivityLevel() {
        // Given
        int userId = 1;
        double weight = 70.0;
        double height = 170.0;
        int age = 25;
        String gender = "M";
        String activityLevel = "invalid"; // 无效活动水平
        double targetWeight = 65.0;

        // When
        HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
            userId, weight, height, age, gender, activityLevel, targetWeight);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getHealthData()).isNull();
        assertThat(result.getMessage()).isEqualTo("活动水平必须是有效的值");
        verify(healthDataDao, never()).getLatestHealthDataByUserId(anyInt());
        verify(healthDataDao, never()).addHealthData(any(UserHealthData.class));
        verify(healthDataDao, never()).updateHealthData(any(UserHealthData.class));
    }

    @Test
    void testSaveHealthDataInvalidTargetWeight() {
        // Given
        int userId = 1;
        double weight = 70.0;
        double height = 170.0;
        int age = 25;
        String gender = "M";
        String activityLevel = "moderate";
        double targetWeight = -10.0; // 无效目标体重

        // When
        HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
            userId, weight, height, age, gender, activityLevel, targetWeight);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getHealthData()).isNull();
        assertThat(result.getMessage()).isEqualTo("目标体重必须在0-300kg之间");
        verify(healthDataDao, never()).getLatestHealthDataByUserId(anyInt());
        verify(healthDataDao, never()).addHealthData(any(UserHealthData.class));
        verify(healthDataDao, never()).updateHealthData(any(UserHealthData.class));
    }

    @Test
    void testSaveHealthDataDatabaseFailure() {
        // Given
        int userId = 1;
        double weight = 70.0;
        double height = 170.0;
        int age = 25;
        String gender = "M";
        String activityLevel = "moderate";
        double targetWeight = 65.0;

        when(healthDataDao.getLatestHealthDataByUserId(userId)).thenReturn(null);
        when(healthDataDao.addHealthData(any(UserHealthData.class))).thenReturn(false);

        // When
        HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
            userId, weight, height, age, gender, activityLevel, targetWeight);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getHealthData()).isNull();
        assertThat(result.getMessage()).isEqualTo("健康数据保存失败，请稍后重试");
        verify(healthDataDao).getLatestHealthDataByUserId(userId);
        verify(healthDataDao).addHealthData(any(UserHealthData.class));
    }

    @Test
    void testGetLatestHealthData() {
        // Given
        int userId = 1;
        UserHealthData expectedData = createHealthData(userId, 70.0, 170.0, 25, "M", "moderate", 65.0);
        when(healthDataDao.getLatestHealthDataByUserId(userId)).thenReturn(expectedData);

        // When
        UserHealthData result = healthDataService.getLatestHealthData(userId);

        // Then
        assertThat(result).isEqualTo(expectedData);
        verify(healthDataDao).getLatestHealthDataByUserId(userId);
    }

    @Test
    void testGetAllHealthData() {
        // Given
        int userId = 1;
        List<UserHealthData> expectedData = Arrays.asList(
            createHealthData(userId, 70.0, 170.0, 25, "M", "moderate", 65.0),
            createHealthData(userId, 72.0, 170.0, 25, "M", "moderate", 65.0)
        );
        when(healthDataDao.getHealthDataByUserId(userId)).thenReturn(expectedData);

        // When
        List<UserHealthData> result = healthDataService.getAllHealthData(userId);

        // Then
        assertThat(result).isEqualTo(expectedData);
        verify(healthDataDao).getHealthDataByUserId(userId);
    }

    @Test
    void testGetHealthReportWithData() {
        // Given
        int userId = 1;
        UserHealthData healthData = createHealthData(userId, 70.0, 170.0, 25, "M", "moderate", 65.0);
        when(healthDataDao.getLatestHealthDataByUserId(userId)).thenReturn(healthData);

        // When
        String result = healthDataService.getHealthReport(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).contains("=== 个人健康报告 ===");
        assertThat(result).contains("身高: 170.0cm");
        assertThat(result).contains("体重: 70.0kg");
        assertThat(result).contains("年龄: 25岁");
        assertThat(result).contains("性别: 男");
        assertThat(result).contains("活动水平: 中度活动（每周运动3-5次）");
        assertThat(result).contains("BMI指数:");
        assertThat(result).contains("基础代谢率:");
        assertThat(result).contains("每日总能量消耗:");
        assertThat(result).contains("理想体重范围:");
        assertThat(result).contains("需要减重:");
        assertThat(result).contains("健康建议:");
        verify(healthDataDao).getLatestHealthDataByUserId(userId);
    }

    @Test
    void testGetHealthReportWithoutData() {
        // Given
        int userId = 1;
        when(healthDataDao.getLatestHealthDataByUserId(userId)).thenReturn(null);

        // When
        String result = healthDataService.getHealthReport(userId);

        // Then
        assertThat(result).isEqualTo("暂无健康数据，请先完善您的健康信息。");
        verify(healthDataDao).getLatestHealthDataByUserId(userId);
    }

    @Test
    void testSaveDailyCheckInSuccessNew() {
        // Given
        int userId = 1;
        String mood = "good";
        double sleepHours = 8.0;
        int waterIntake = 2000;
        int exerciseMinutes = 30;
        String notes = "今天感觉不错";

        LocalDate today = LocalDate.now();
        when(dailyCheckInDao.getCheckInByUserIdAndDate(userId, today)).thenReturn(null);
        when(dailyCheckInDao.addCheckIn(any(DailyCheckIn.class))).thenReturn(true);
        when(dailyCheckInDao.getConsecutiveCheckInDays(userId)).thenReturn(1);

        // When
        HealthDataService.CheckInResult result = healthDataService.saveDailyCheckIn(
            userId, mood, sleepHours, waterIntake, exerciseMinutes, notes);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getCheckIn()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("打卡成功！连续打卡1天");
        verify(dailyCheckInDao).getCheckInByUserIdAndDate(userId, today);
        verify(dailyCheckInDao).addCheckIn(any(DailyCheckIn.class));
        verify(dailyCheckInDao, never()).updateCheckIn(any(DailyCheckIn.class));
        verify(dailyCheckInDao).getConsecutiveCheckInDays(userId);
    }

    @Test
    void testSaveDailyCheckInSuccessUpdate() {
        // Given
        int userId = 1;
        String mood = "good";
        double sleepHours = 8.0;
        int waterIntake = 2000;
        int exerciseMinutes = 30;
        String notes = "今天感觉不错";

        LocalDate today = LocalDate.now();
        DailyCheckIn existingCheckIn = createCheckIn(userId, today, "normal", 7.0, 1500, 0, "昨天的记录");
        when(dailyCheckInDao.getCheckInByUserIdAndDate(userId, today)).thenReturn(existingCheckIn);
        when(dailyCheckInDao.updateCheckIn(any(DailyCheckIn.class))).thenReturn(true);
        when(dailyCheckInDao.getConsecutiveCheckInDays(userId)).thenReturn(2);

        // When
        HealthDataService.CheckInResult result = healthDataService.saveDailyCheckIn(
            userId, mood, sleepHours, waterIntake, exerciseMinutes, notes);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getCheckIn()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("打卡成功！连续打卡2天");
        verify(dailyCheckInDao).getCheckInByUserIdAndDate(userId, today);
        verify(dailyCheckInDao, never()).addCheckIn(any(DailyCheckIn.class));
        verify(dailyCheckInDao).updateCheckIn(any(DailyCheckIn.class));
        verify(dailyCheckInDao).getConsecutiveCheckInDays(userId);
    }

    @Test
    void testSaveDailyCheckInInvalidMood() {
        // Given
        int userId = 1;
        String mood = "invalid"; // 无效心情
        double sleepHours = 8.0;
        int waterIntake = 2000;
        int exerciseMinutes = 30;
        String notes = "今天感觉不错";

        // When
        HealthDataService.CheckInResult result = healthDataService.saveDailyCheckIn(
            userId, mood, sleepHours, waterIntake, exerciseMinutes, notes);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getCheckIn()).isNull();
        assertThat(result.getMessage()).isEqualTo("心情必须是有效的值");
        verify(dailyCheckInDao, never()).getCheckInByUserIdAndDate(anyInt(), any(LocalDate.class));
        verify(dailyCheckInDao, never()).addCheckIn(any(DailyCheckIn.class));
        verify(dailyCheckInDao, never()).updateCheckIn(any(DailyCheckIn.class));
    }

    @Test
    void testSaveDailyCheckInInvalidSleepHours() {
        // Given
        int userId = 1;
        String mood = "good";
        double sleepHours = -1.0; // 无效睡眠时长
        int waterIntake = 2000;
        int exerciseMinutes = 30;
        String notes = "今天感觉不错";

        // When
        HealthDataService.CheckInResult result = healthDataService.saveDailyCheckIn(
            userId, mood, sleepHours, waterIntake, exerciseMinutes, notes);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getCheckIn()).isNull();
        assertThat(result.getMessage()).isEqualTo("睡眠时长必须在0-24小时之间");
        verify(dailyCheckInDao, never()).getCheckInByUserIdAndDate(anyInt(), any(LocalDate.class));
        verify(dailyCheckInDao, never()).addCheckIn(any(DailyCheckIn.class));
        verify(dailyCheckInDao, never()).updateCheckIn(any(DailyCheckIn.class));
    }

    @Test
    void testSaveDailyCheckInInvalidWaterIntake() {
        // Given
        int userId = 1;
        String mood = "good";
        double sleepHours = 8.0;
        int waterIntake = -100; // 无效饮水量
        int exerciseMinutes = 30;
        String notes = "今天感觉不错";

        // When
        HealthDataService.CheckInResult result = healthDataService.saveDailyCheckIn(
            userId, mood, sleepHours, waterIntake, exerciseMinutes, notes);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getCheckIn()).isNull();
        assertThat(result.getMessage()).isEqualTo("饮水量必须在0-10000毫升之间");
        verify(dailyCheckInDao, never()).getCheckInByUserIdAndDate(anyInt(), any(LocalDate.class));
        verify(dailyCheckInDao, never()).addCheckIn(any(DailyCheckIn.class));
        verify(dailyCheckInDao, never()).updateCheckIn(any(DailyCheckIn.class));
    }

    @Test
    void testSaveDailyCheckInInvalidExerciseMinutes() {
        // Given
        int userId = 1;
        String mood = "good";
        double sleepHours = 8.0;
        int waterIntake = 2000;
        int exerciseMinutes = -10; // 无效运动时长
        String notes = "今天感觉不错";

        // When
        HealthDataService.CheckInResult result = healthDataService.saveDailyCheckIn(
            userId, mood, sleepHours, waterIntake, exerciseMinutes, notes);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getCheckIn()).isNull();
        assertThat(result.getMessage()).isEqualTo("运动时长必须在0-1440分钟之间");
        verify(dailyCheckInDao, never()).getCheckInByUserIdAndDate(anyInt(), any(LocalDate.class));
        verify(dailyCheckInDao, never()).addCheckIn(any(DailyCheckIn.class));
        verify(dailyCheckInDao, never()).updateCheckIn(any(DailyCheckIn.class));
    }

    @Test
    void testSaveDailyCheckInDatabaseFailure() {
        // Given
        int userId = 1;
        String mood = "good";
        double sleepHours = 8.0;
        int waterIntake = 2000;
        int exerciseMinutes = 30;
        String notes = "今天感觉不错";

        LocalDate today = LocalDate.now();
        when(dailyCheckInDao.getCheckInByUserIdAndDate(userId, today)).thenReturn(null);
        when(dailyCheckInDao.addCheckIn(any(DailyCheckIn.class))).thenReturn(false);

        // When
        HealthDataService.CheckInResult result = healthDataService.saveDailyCheckIn(
            userId, mood, sleepHours, waterIntake, exerciseMinutes, notes);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getCheckIn()).isNull();
        assertThat(result.getMessage()).isEqualTo("打卡失败，请稍后重试");
        verify(dailyCheckInDao).getCheckInByUserIdAndDate(userId, today);
        verify(dailyCheckInDao).addCheckIn(any(DailyCheckIn.class));
    }

    @Test
    void testGetTodayCheckIn() {
        // Given
        int userId = 1;
        LocalDate today = LocalDate.now();
        DailyCheckIn expectedCheckIn = createCheckIn(userId, today, "good", 8.0, 2000, 30, "今天感觉不错");
        when(dailyCheckInDao.getCheckInByUserIdAndDate(userId, today)).thenReturn(expectedCheckIn);

        // When
        DailyCheckIn result = healthDataService.getTodayCheckIn(userId);

        // Then
        assertThat(result).isEqualTo(expectedCheckIn);
        verify(dailyCheckInDao).getCheckInByUserIdAndDate(userId, today);
    }

    @Test
    void testHasCheckedInTodayTrue() {
        // Given
        int userId = 1;
        LocalDate today = LocalDate.now();
        DailyCheckIn checkIn = createCheckIn(userId, today, "good", 8.0, 2000, 30, "今天感觉不错");
        when(dailyCheckInDao.hasCheckedInToday(userId)).thenReturn(true);

        // When
        boolean result = healthDataService.hasCheckedInToday(userId);

        // Then
        assertThat(result).isTrue();
        verify(dailyCheckInDao).hasCheckedInToday(userId);
    }

    @Test
    void testHasCheckedInTodayFalse() {
        // Given
        int userId = 1;
        when(dailyCheckInDao.hasCheckedInToday(userId)).thenReturn(false);

        // When
        boolean result = healthDataService.hasCheckedInToday(userId);

        // Then
        assertThat(result).isFalse();
        verify(dailyCheckInDao).hasCheckedInToday(userId);
    }

    @Test
    void testGetRecentCheckIns() {
        // Given
        int userId = 1;
        int days = 7;
        List<DailyCheckIn> expectedCheckIns = Arrays.asList(
            createCheckIn(userId, LocalDate.now().minusDays(1), "good", 8.0, 2000, 30, "昨天"),
            createCheckIn(userId, LocalDate.now().minusDays(2), "normal", 7.0, 1500, 0, "前天")
        );
        when(dailyCheckInDao.getRecentCheckIns(userId, days)).thenReturn(expectedCheckIns);

        // When
        List<DailyCheckIn> result = healthDataService.getRecentCheckIns(userId, days);

        // Then
        assertThat(result).isEqualTo(expectedCheckIns);
        verify(dailyCheckInDao).getRecentCheckIns(userId, days);
    }

    @Test
    void testGetHealthStatistics() {
        // Given
        int userId = 1;
        int days = 30;
        DailyCheckInDao.HealthStatistics expectedStats = mock(DailyCheckInDao.HealthStatistics.class);
        when(dailyCheckInDao.getHealthStatistics(userId, days)).thenReturn(expectedStats);

        // When
        DailyCheckInDao.HealthStatistics result = healthDataService.getHealthStatistics(userId, days);

        // Then
        assertThat(result).isEqualTo(expectedStats);
        verify(dailyCheckInDao).getHealthStatistics(userId, days);
    }

    @Test
    void testGetConsecutiveCheckInDays() {
        // Given
        int userId = 1;
        int consecutiveDays = 5;
        when(dailyCheckInDao.getConsecutiveCheckInDays(userId)).thenReturn(consecutiveDays);

        // When
        int result = healthDataService.getConsecutiveCheckInDays(userId);

        // Then
        assertThat(result).isEqualTo(consecutiveDays);
        verify(dailyCheckInDao).getConsecutiveCheckInDays(userId);
    }

    @Test
    void testValidateHealthDataInputValid() {
        // Given
        double weight = 70.0;
        double height = 170.0;
        int age = 25;
        String gender = "M";
        String activityLevel = "moderate";
        double targetWeight = 65.0;

        // When & Then - 通过公共方法测试私有验证逻辑
        HealthDataService.HealthDataResult result = healthDataService.saveHealthData(
            1, weight, height, age, gender, activityLevel, targetWeight);

        // 验证通过的情况应该到达数据库调用阶段
        assertThat(result.isSuccess() || result.getMessage().contains("保存失败")).isTrue();
    }

    @Test
    void testValidateCheckInInputValid() {
        // Given
        String mood = "good";
        double sleepHours = 8.0;
        int waterIntake = 2000;
        int exerciseMinutes = 30;

        // When & Then - 通过公共方法测试私有验证逻辑
        HealthDataService.CheckInResult result = healthDataService.saveDailyCheckIn(
            1, mood, sleepHours, waterIntake, exerciseMinutes, "有效备注");

        // 验证通过的情况应该到达数据库调用阶段
        assertThat(result.isSuccess() || result.getMessage().contains("失败")).isTrue();
    }
}
