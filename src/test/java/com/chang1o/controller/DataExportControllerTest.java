package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.service.DataExportService;
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
class DataExportControllerTest {

    @Mock
    private DataExportService dataExportService;

    @Mock
    private SessionManager sessionManager;

    private DataExportController dataExportController;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        // 设置输出捕获
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        dataExportController = new DataExportController();
        
        // 使用反射注入mock对象
        try {
            java.lang.reflect.Field field = DataExportController.class.getSuperclass()
                .getDeclaredField("sessionManager");
            field.setAccessible(true);
            field.set(dataExportController, sessionManager);
            
            field = DataExportController.class.getDeclaredField("dataExportService");
            field.setAccessible(true);
            field.set(dataExportController, dataExportService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testExportDataWithCustomFilePath() {
        // Given
        User user = createUser(1, "testuser");
        String input = "/custom/path/export.txt\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock所有导出服务都成功
        when(dataExportService.exportHealthDataReport(1)).thenReturn("/custom/path/health_export.txt");
        when(dataExportService.exportRecipeData(1)).thenReturn("/custom/path/recipe_export.txt");
        when(dataExportService.exportPantryData(1)).thenReturn("/custom/path/pantry_export.txt");
        when(dataExportService.exportCheckInRecords(1, 30)).thenReturn("/custom/path/checkin_export.txt");

        // When
        dataExportController.exportData(user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("数据导出");
        assertThat(output).contains("请输入导出文件路劲");
        assertThat(output).contains("数据导出成功");
        assertThat(output).contains("健康数据存放在：");
        assertThat(output).contains("食谱数据存放在：");
        assertThat(output).contains("库存数据存放在：");
        assertThat(output).contains("打卡数据存放在：");
        
        verify(dataExportService).exportHealthDataReport(1);
        verify(dataExportService).exportRecipeData(1);
        verify(dataExportService).exportPantryData(1);
        verify(dataExportService).exportCheckInRecords(1, 30);
    }

    @Test
    void testExportDataWithDefaultFilePath() {
        // Given
        User user = createUser(1, "testuser");
        String input = "\n"; // 直接回车，使用默认路径
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock所有导出服务都成功
        when(dataExportService.exportHealthDataReport(1)).thenReturn("user_data_export.txt");
        when(dataExportService.exportRecipeData(1)).thenReturn("user_data_export.txt");
        when(dataExportService.exportPantryData(1)).thenReturn("user_data_export.txt");
        when(dataExportService.exportCheckInRecords(1, 30)).thenReturn("user_data_export.txt");

        // When
        dataExportController.exportData(user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("数据导出");
        assertThat(output).contains("正在导出个人数据");
        assertThat(output).contains("数据导出成功");
        assertThat(output).contains("健康数据存放在：");
        
        verify(dataExportService).exportHealthDataReport(1);
        verify(dataExportService).exportRecipeData(1);
        verify(dataExportService).exportPantryData(1);
        verify(dataExportService).exportCheckInRecords(1, 30);
    }

    @Test
    void testExportDataPartialSuccess() {
        // Given
        User user = createUser(1, "testuser");
        String input = "/path/export.txt\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock部分导出成功，部分失败
        when(dataExportService.exportHealthDataReport(1)).thenReturn("/path/health_export.txt");
        when(dataExportService.exportRecipeData(1)).thenReturn(null); // 失败
        when(dataExportService.exportPantryData(1)).thenReturn("/path/pantry_export.txt");
        when(dataExportService.exportCheckInRecords(1, 30)).thenReturn(null); // 失败

        // When
        dataExportController.exportData(user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("数据导出成功");
        assertThat(output).contains("健康数据存放在：");
        assertThat(output).contains("库存数据存放在：");
        assertThat(output).doesNotContain("食谱数据存放在：");
        assertThat(output).doesNotContain("打卡数据存放在：");
        
        verify(dataExportService).exportHealthDataReport(1);
        verify(dataExportService).exportRecipeData(1);
        verify(dataExportService).exportPantryData(1);
        verify(dataExportService).exportCheckInRecords(1, 30);
    }

    @Test
    void testExportDataAllFailure() {
        // Given
        User user = createUser(1, "testuser");
        String input = "/path/export.txt\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock所有导出都失败
        when(dataExportService.exportHealthDataReport(1)).thenReturn(null);
        when(dataExportService.exportRecipeData(1)).thenReturn(null);
        when(dataExportService.exportPantryData(1)).thenReturn(null);
        when(dataExportService.exportCheckInRecords(1, 30)).thenReturn(null);

        // When
        dataExportController.exportData(user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("数据导出失败，请检查是否有写入权限");
        
        verify(dataExportService).exportHealthDataReport(1);
        verify(dataExportService).exportRecipeData(1);
        verify(dataExportService).exportPantryData(1);
        verify(dataExportService).exportCheckInRecords(1, 30);
    }

    @Test
    void testExportDataOnlyHealthDataSuccess() {
        // Given
        User user = createUser(1, "testuser");
        String input = "/path/export.txt\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock只有健康数据导出成功
        when(dataExportService.exportHealthDataReport(1)).thenReturn("/path/health_export.txt");
        when(dataExportService.exportRecipeData(1)).thenReturn(null);
        when(dataExportService.exportPantryData(1)).thenReturn(null);
        when(dataExportService.exportCheckInRecords(1, 30)).thenReturn(null);

        // When
        dataExportController.exportData(user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("数据导出成功");
        assertThat(output).contains("健康数据存放在：");
        assertThat(output).doesNotContain("食谱数据存放在：");
        assertThat(output).doesNotContain("库存数据存放在：");
        assertThat(output).doesNotContain("打卡数据存放在：");
    }

    @Test
    void testExportDataOnlyCheckInDataSuccess() {
        // Given
        User user = createUser(1, "testuser");
        String input = "/path/export.txt\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock只有打卡数据导出成功
        when(dataExportService.exportHealthDataReport(1)).thenReturn(null);
        when(dataExportService.exportRecipeData(1)).thenReturn(null);
        when(dataExportService.exportPantryData(1)).thenReturn(null);
        when(dataExportService.exportCheckInRecords(1, 30)).thenReturn("/path/checkin_export.txt");

        // When
        dataExportController.exportData(user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("数据导出成功");
        assertThat(output).contains("打卡数据存放在：");
        assertThat(output).doesNotContain("健康数据存放在：");
        assertThat(output).doesNotContain("食谱数据存放在：");
        assertThat(output).doesNotContain("库存数据存放在：");
    }

    @Test
    void testExportDataWithEmptyInput() {
        // Given
        User user = createUser(1, "testuser");
        String input = ""; // 空输入
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock默认路径
        when(dataExportService.exportHealthDataReport(1)).thenReturn("user_data_export.txt");

        // When
        dataExportController.exportData(user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("数据导出");
        assertThat(output).contains("正在导出个人数据");
        
        verify(dataExportService).exportHealthDataReport(1);
    }

    private User createUser(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }
}
