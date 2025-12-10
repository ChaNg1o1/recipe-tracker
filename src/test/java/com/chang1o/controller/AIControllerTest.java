package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.model.Recipe;
import com.chang1o.service.KimiApiService;
import com.chang1o.service.RecipeService;
import com.chang1o.service.PantryService;
import com.chang1o.session.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIControllerTest {

    @Mock
    private KimiApiService kimiApiService;

    @Mock
    private RecipeService recipeService;

    @Mock
    private PantryService pantryService;

    @Mock
    private SessionManager sessionManager;

    private AIController aiController;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        // 设置输出捕获
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        aiController = new AIController();
        
        // 使用反射注入mock对象
        try {
            java.lang.reflect.Field field = AIController.class.getSuperclass()
                .getDeclaredField("sessionManager");
            field.setAccessible(true);
            field.set(aiController, sessionManager);
            
            field = AIController.class.getDeclaredField("kimiApiService");
            field.setAccessible(true);
            field.set(aiController, kimiApiService);
            
            field = AIController.class.getDeclaredField("recipeService");
            field.setAccessible(true);
            field.set(aiController, recipeService);
            
            field = AIController.class.getDeclaredField("pantryService");
            field.setAccessible(true);
            field.set(aiController, pantryService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShowMenuWithValidChoices() {
        // Given
        User user = createUser(1, "testuser");
        String input = "1\n0\n"; // 选择健康建议然后退出
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock API服务
        when(kimiApiService.generatePersonalizedHealthAdvice(1))
            .thenReturn("个性化健康建议内容");

        // When
        aiController.showMenu(user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("请输入您的选择");
        assertThat(output).contains("个性化健康建议");
        verify(kimiApiService).generatePersonalizedHealthAdvice(1);
    }

    @Test
    void testShowMenuWithInvalidChoice() {
        // Given
        User user = createUser(1, "testuser");
        String input = "99\n0\n"; // 无效选择然后退出
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        // When
        aiController.showMenu(user);

        // Then
        assertThat(outContent.toString()).contains("无效的选择，请输入0-4之间的数字！");
    }

    @Test
    void testHandlePersonalizedHealthAdviceSuccess() {
        // Given
        User user = createUser(1, "testuser");
        String expectedAdvice = "基于您的健康数据，建议您多运动，保持良好作息。";
        when(kimiApiService.generatePersonalizedHealthAdvice(1)).thenReturn(expectedAdvice);

        // When
        invokePrivateMethod(aiController, "handlePersonalizedHealthAdvice", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("个性化健康建议");
        assertThat(output).contains(expectedAdvice);
        verify(kimiApiService).generatePersonalizedHealthAdvice(1);
    }

    @Test
    void testHandlePersonalizedHealthAdviceException() {
        // Given
        User user = createUser(1, "testuser");
        when(kimiApiService.generatePersonalizedHealthAdvice(1))
            .thenThrow(new RuntimeException("API服务异常"));

        // When
        invokePrivateMethod(aiController, "handlePersonalizedHealthAdvice", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("获取健康建议时出错");
        assertThat(output).contains("建议您先完善健康数据后再试");
        verify(kimiApiService).generatePersonalizedHealthAdvice(1);
    }

    @Test
    void testHandleSmartRecipeRecommendationsSuccess() {
        // Given
        User user = createUser(1, "testuser");
        List<String> recommendations = Arrays.asList(
            "推荐食谱1：番茄炒蛋",
            "推荐食谱2：宫保鸡丁",
            "推荐食谱3：麻婆豆腐"
        );
        when(kimiApiService.generateSmartRecipeRecommendations(1)).thenReturn(recommendations);

        // When
        invokePrivateMethod(aiController, "handleSmartRecipeRecommendations", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("智能食谱推荐");
        assertThat(output).contains("推荐食谱1");
        assertThat(output).contains("推荐食谱2");
        assertThat(output).contains("推荐食谱3");
        verify(kimiApiService).generateSmartRecipeRecommendations(1);
    }

    @Test
    void testHandleSmartRecipeRecommendationsException() {
        // Given
        User user = createUser(1, "testuser");
        when(kimiApiService.generateSmartRecipeRecommendations(1))
            .thenThrow(new RuntimeException("推荐服务异常"));

        // When
        invokePrivateMethod(aiController, "handleSmartRecipeRecommendations", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("获取食谱推荐时出错");
        assertThat(output).contains("建议您先添加一些食谱数据后再试");
        verify(kimiApiService).generateSmartRecipeRecommendations(1);
    }

    @Test
    void testHandleSmartShoppingListBasedOnPantry() {
        // Given
        User user = createUser(1, "testuser");
        String input = "1\n"; // 选择基于当前库存生成
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        String expectedShoppingList = "基于您的库存，建议购买：面粉、鸡蛋、牛奶";
        when(kimiApiService.generateSmartShoppingList(eq(1), anyList()))
            .thenReturn(expectedShoppingList);

        // When
        invokePrivateMethod(aiController, "handleSmartShoppingList", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("智能购物清单");
        assertThat(output).contains("基于当前库存生成");
        assertThat(output).contains(expectedShoppingList);
        verify(kimiApiService).generateSmartShoppingList(eq(1), anyList());
    }

    @Test
    void testHandleSmartShoppingListBasedOnRecipes() {
        // Given
        User user = createUser(1, "testuser");
        String input = "2\n1\n"; // 选择基于食谱生成，选择第一个食谱
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        // Mock用户食谱列表
        List<Recipe> userRecipes = Arrays.asList(
            createRecipe(1, "红烧肉", "制作红烧肉", 1, 1)
        );
        when(recipeService.getRecipesByUser(1)).thenReturn(userRecipes);
        
        String expectedShoppingList = "制作红烧肉需要：五花肉、生抽、老抽、冰糖";
        when(kimiApiService.generateSmartShoppingList(eq(1), anyList()))
            .thenReturn(expectedShoppingList);

        // When
        invokePrivateMethod(aiController, "handleSmartShoppingList", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("智能购物清单");
        assertThat(output).contains("基于选定食谱生成");
        assertThat(output).contains("红烧肉");
        assertThat(output).contains(expectedShoppingList);
        verify(recipeService).getRecipesByUser(1);
        verify(kimiApiService).generateSmartShoppingList(eq(1), anyList());
    }

    @Test
    void testHandleSmartShoppingListNoRecipes() {
        // Given
        User user = createUser(1, "testuser");
        String input = "2\n"; // 选择基于食谱生成
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        when(recipeService.getRecipesByUser(1)).thenReturn(Arrays.asList()); // 空食谱列表

        // When
        invokePrivateMethod(aiController, "handleSmartShoppingList", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("您还没有添加任何食谱");
        verify(recipeService).getRecipesByUser(1);
        verify(kimiApiService, never()).generateSmartShoppingList(anyInt(), anyList());
    }

    @Test
    void testHandleSmartShoppingListInvalidChoice() {
        // Given
        User user = createUser(1, "testuser");
        String input = "99\n"; // 无效选择
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        // When
        invokePrivateMethod(aiController, "handleSmartShoppingList", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("无效的选择");
        verify(kimiApiService, never()).generateSmartShoppingList(anyInt(), anyList());
    }

    @Test
    void testHandleNutritionAnalysisSuccess() {
        // Given
        User user = createUser(1, "testuser");
        String input = "7\n"; // 分析7天
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        String expectedReport = "过去7天的营养分析：您的蛋白质摄入不足，建议增加鱼肉摄入。";
        when(kimiApiService.generateNutritionAnalysisReport(1, 7)).thenReturn(expectedReport);

        // When
        invokePrivateMethod(aiController, "handleNutritionAnalysis", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("营养分析报告");
        assertThat(output).contains("分析天数");
        assertThat(output).contains("7天");
        assertThat(output).contains(expectedReport);
        verify(kimiApiService).generateNutritionAnalysisReport(1, 7);
    }

    @Test
    void testHandleNutritionAnalysisDefaultDays() {
        // Given
        User user = createUser(1, "testuser");
        String input = "\n"; // 直接回车，使用默认天数
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        String expectedReport = "过去7天的营养分析报告";
        when(kimiApiService.generateNutritionAnalysisReport(1, 7)).thenReturn(expectedReport);

        // When
        invokePrivateMethod(aiController, "handleNutritionAnalysis", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("营养分析报告");
        assertThat(output).contains("默认7天");
        verify(kimiApiService).generateNutritionAnalysisReport(1, 7);
    }

    @Test
    void testHandleNutritionAnalysisInvalidInput() {
        // Given
        User user = createUser(1, "testuser");
        String input = "invalid\n7\n"; // 无效输入，然后输入有效天数
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        String expectedReport = "过去7天的营养分析报告";
        when(kimiApiService.generateNutritionAnalysisReport(1, 7)).thenReturn(expectedReport);

        // When
        invokePrivateMethod(aiController, "handleNutritionAnalysis", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("输入无效，使用默认7天");
        verify(kimiApiService).generateNutritionAnalysisReport(1, 7);
    }

    @Test
    void testHandleNutritionAnalysisException() {
        // Given
        User user = createUser(1, "testuser");
        String input = "7\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);
        
        when(kimiApiService.generateNutritionAnalysisReport(1, 7))
            .thenThrow(new RuntimeException("分析服务异常"));

        // When
        invokePrivateMethod(aiController, "handleNutritionAnalysis", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("生成营养分析时出错");
        assertThat(output).contains("建议您先进行每日打卡后再试");
        verify(kimiApiService).generateNutritionAnalysisReport(1, 7);
    }

    private User createUser(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private Recipe createRecipe(int id, String name, String instructions, int categoryId, int userId) {
        Recipe recipe = new Recipe(name, instructions, categoryId, userId);
        recipe.setId(id);
        return recipe;
    }

    private void invokePrivateMethod(Object object, String methodName, Object... args) {
        try {
            Class<?>[] paramTypes = java.util.Arrays.stream(args)
                .map(arg -> arg.getClass())
                .toArray(Class<?>[]::new);
            java.lang.reflect.Method method = object.getClass()
                .getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            method.invoke(object, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}