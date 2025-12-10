package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.model.Recipe;
import com.chang1o.model.Category;
import com.chang1o.service.RecipeService;
import com.chang1o.session.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @Mock
    private SessionManager sessionManager;

    private RecipeController recipeController;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        // 设置输出捕获
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        recipeController = new RecipeController();
        
        // 使用反射注入mock对象
        try {
            java.lang.reflect.Field field = RecipeController.class.getSuperclass()
                .getDeclaredField("sessionManager");
            field.setAccessible(true);
            field.set(recipeController, sessionManager);
            
            field = RecipeController.class.getDeclaredField("recipeService");
            field.setAccessible(true);
            field.set(recipeController, recipeService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShowMenuWithValidChoices() {
        // Given
        User user = createUser(1, "testuser");
        String input = "2\n0\n"; // 查看我的食谱然后退出
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        // Mock recipe service for viewMyRecipes
        when(recipeService.getRecipesByUser(1)).thenReturn(Arrays.asList());

        // When
        recipeController.showMenu(user);

        // Then
        assertThat(outContent.toString()).contains("请输入您的选择");
        verify(recipeService).getRecipesByUser(1);
    }

    @Test
    void testShowMenuWithInvalidChoice() {
        // Given
        User user = createUser(1, "testuser");
        String input = "99\n0\n"; // 无效选择然后退出
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        // When
        recipeController.showMenu(user);

        // Then
        assertThat(outContent.toString()).contains("无效的选择，请重新输入！");
    }

    @Test
    void testViewMyRecipesWithItems() {
        // Given
        User user = createUser(1, "testuser");
        List<Recipe> recipes = Arrays.asList(createRecipe(1, "测试食谱", "制作步骤", 1, 1));
        when(recipeService.getRecipesByUser(1)).thenReturn(recipes);

        // When
        invokePrivateMethod(recipeController, "viewMyRecipes", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("我的食谱");
        assertThat(output).contains("您的食谱列表");
        assertThat(output).contains("测试食谱");
        verify(recipeService).getRecipesByUser(1);
    }

    @Test
    void testViewMyRecipesEmpty() {
        // Given
        User user = createUser(1, "testuser");
        when(recipeService.getRecipesByUser(1)).thenReturn(Arrays.asList());

        // When
        invokePrivateMethod(recipeController, "viewMyRecipes", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("我的食谱");
        assertThat(output).contains("您还没有添加任何食谱");
        verify(recipeService).getRecipesByUser(1);
    }

    @Test
    void testSearchRecipesWithResults() {
        // Given
        String input = "红烧\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        List<Recipe> recipes = Arrays.asList(createRecipe(1, "红烧肉", "制作红烧肉的步骤", 1, 1));
        when(recipeService.searchRecipes("红烧")).thenReturn(recipes);

        // When
        invokePrivateMethod(recipeController, "searchRecipes");

        // Then
        String output = outContent.toString();
        assertThat(output).contains("搜索食谱");
        assertThat(output).contains("搜索结果");
        assertThat(output).contains("红烧肉");
        verify(recipeService).searchRecipes("红烧");
    }

    @Test
    void testSearchRecipesNoResults() {
        // Given
        String input = "不存在的食谱\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        when(recipeService.searchRecipes("不存在的食谱")).thenReturn(Arrays.asList());

        // When
        invokePrivateMethod(recipeController, "searchRecipes");

        // Then
        String output = outContent.toString();
        assertThat(output).contains("搜索食谱");
        assertThat(output).contains("未找到相关食谱");
        verify(recipeService).searchRecipes("不存在的食谱");
    }

    @Test
    void testViewAllRecipes() {
        // Given
        List<Recipe> recipes = Arrays.asList(
            createRecipe(1, "食谱1", "步骤1", 1, 1),
            createRecipe(2, "食谱2", "步骤2", 1, 2)
        );
        when(recipeService.getAllRecipes()).thenReturn(recipes);

        // When
        invokePrivateMethod(recipeController, "viewAllRecipes");

        // Then
        String output = outContent.toString();
        assertThat(output).contains("所有食谱");
        assertThat(output).contains("食谱1");
        assertThat(output).contains("食谱2");
        verify(recipeService).getAllRecipes();
    }

    @Test
    void testViewAllRecipesEmpty() {
        // Given
        when(recipeService.getAllRecipes()).thenReturn(Arrays.asList());

        // When
        invokePrivateMethod(recipeController, "viewAllRecipes");

        // Then
        String output = outContent.toString();
        assertThat(output).contains("所有食谱");
        assertThat(output).contains("暂无食谱");
        verify(recipeService).getAllRecipes();
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
        
        // 设置分类信息
        Category category = new Category(categoryId, "测试分类");
        recipe.setCategory(category);
        
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