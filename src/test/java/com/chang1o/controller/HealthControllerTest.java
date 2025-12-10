package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.model.UserHealthData;
import com.chang1o.service.HealthDataService;
import com.chang1o.dao.DailyCheckInDao;
import com.chang1o.session.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    @Mock
    private HealthDataService healthDataService;

    @Mock
    private SessionManager sessionManager;

    private HealthController healthController;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        // 设置输出捕获
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        healthController = new HealthController();
        
        // 使用反射注入mock对象
        try {
            java.lang.reflect.Field field = HealthController.class.getSuperclass()
                .getDeclaredField("sessionManager");
            field.setAccessible(true);
            field.set(healthController, sessionManager);
            
            field = HealthController.class.getDeclaredField("healthDataService");
            field.setAccessible(true);
            field.set(healthController, healthDataService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShowMenuWithValidChoices() {
        // Given
        User user = createUser(1, "testuser");
        String input = "1\n0\n"; // 选择管理健康数据然后退出
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock健康数据服务
        when(healthDataService.saveHealthData(eq(1), anyDouble(), anyDouble(), anyInt(), anyString(), anyString(), anyDouble()))
            .thenReturn(null); // 简化mock返回

        // When
        healthController.showMenu(user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("请输入您的选择");
        assertThat(output).contains("管理健康数据");
        verify(healthDataService).saveHealthData(eq(1), anyDouble(), anyDouble(), anyInt(), anyString(), anyString(), anyDouble());
    }

    @Test
    void testShowMenuWithInvalidChoice() {
        // Given
        User user = createUser(1, "testuser");
        String input = "99\n0\n"; // 无效选择然后退出
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        // When
        healthController.showMenu(user);

        // Then
        assertThat(outContent.toString()).contains("[错误] 无效的选择，请输入 0-4 之间的数字！");
    }

    @Test
    void testManageHealthDataSuccess() {
        // Given
        User user = createUser(1, "testuser");
        String input = "175\n70\n25\nM\n2\n65\n"; // 身高、体重、年龄、性别、活动水平、目标体重
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock成功返回
        when(healthDataService.saveHealthData(eq(1), eq(70.0), eq(175.0), eq(25), eq("M"), eq("normal"), eq(65.0)))
            .thenReturn(null);

        // When
        invokePrivateMethod(healthController, "manageHealthData", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("管理健康数据");
        assertThat(output).contains("BMI指数");
        assertThat(output).contains("基础代谢率");
        verify(healthDataService).saveHealthData(eq(1), eq(70.0), eq(175.0), eq(25), eq("M"), eq("normal"), eq(65.0));
    }

    @Test
    void testDailyCheckInSuccess() {
        // Given
        User user = createUser(1, "testuser");
        String input = "2\n8.0\n2000\n30\n今天感觉不错\n"; // 心情、睡眠、饮水、运动、备注
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock成功返回
        when(healthDataService.saveDailyCheckIn(eq(1), eq("good"), eq(8.0), eq(2000), eq(30), eq("今天感觉不错")))
            .thenReturn(null);

        // When
        invokePrivateMethod(healthController, "dailyCheckIn", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("每日打卡");
        assertThat(output).contains("今日健康评分");
        verify(healthDataService).saveDailyCheckIn(eq(1), eq("good"), eq(8.0), eq(2000), eq(30), eq("今天感觉不错"));
    }

    @Test
    void testShowHealthReportWithData() {
        // Given
        User user = createUser(1, "testuser");
        UserHealthData healthData = createUserHealthData(1, 175.0, 70.0, 25, "M", 65.0);
        when(healthDataService.getLatestHealthData(1)).thenReturn(healthData);

        // When
        invokePrivateMethod(healthController, "showHealthReport", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("健康报告");
        assertThat(output).contains("身高：175 cm");
        assertThat(output).contains("体重：70.0 kg");
        verify(healthDataService).getLatestHealthData(1);
    }

    @Test
    void testShowHealthReportNoData() {
        // Given
        User user = createUser(1, "testuser");
        when(healthDataService.getLatestHealthData(1)).thenReturn(null);

        // When
        invokePrivateMethod(healthController, "showHealthReport", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("暂无健康数据，请先录入健康信息");
        verify(healthDataService).getLatestHealthData(1);
    }

    @Test
    void testShowHealthStatistics() {
        // Given
        User user = createUser(1, "testuser");
        String input = "30\n"; // 统计30天
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock返回统计数据
        when(healthDataService.getHealthStatistics(1, 30)).thenReturn(null);

        // When
        invokePrivateMethod(healthController, "showHealthStatistics", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("健康统计分析");
        assertThat(output).contains("过去30天的健康统计");
        verify(healthDataService).getHealthStatistics(1, 30);
    }

    private User createUser(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private UserHealthData createUserHealthData(int id, double height, double weight, int age, String gender, double targetWeight) {
        UserHealthData data = new UserHealthData();
        data.setId(id);
        data.setUserId(1);
        data.setHeight(height);
        data.setWeight(weight);
        data.setAge(age);
        data.setGender(gender);
        data.setTargetWeight(targetWeight);
        return data;
    }

    private Object invokePrivateMethod(Object object, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = java.util.Arrays.stream(args)
                .map(arg -> arg.getClass())
                .toArray(Class<?>[]::new);
            java.lang.reflect.Method method = object.getClass()
                .getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
