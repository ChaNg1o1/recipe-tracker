package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.model.PantryItem;
import com.chang1o.model.Ingredient;
import com.chang1o.service.PantryService;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PantryControllerTest {

    @Mock
    private PantryService pantryService;

    @Mock
    private SessionManager sessionManager;

    private PantryController pantryController;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        // 设置输出捕获
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        pantryController = new PantryController();
        
        // 使用反射注入mock对象
        try {
            java.lang.reflect.Field field = PantryController.class.getSuperclass()
                .getDeclaredField("sessionManager");
            field.setAccessible(true);
            field.set(pantryController, sessionManager);
            
            field = PantryController.class.getDeclaredField("pantryService");
            field.setAccessible(true);
            field.set(pantryController, pantryService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShowMenuWithValidChoices() {
        // Given
        User user = createUser(1, "testuser");
        String input = "1\n0\n"; // 添加库存然后退出
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        // Mock pantry service for addPantryItem
        PantryService.PantryResult successResult = new PantryService.PantryResult(true, null, "添加成功");
        when(pantryService.addPantryItem(anyInt(), anyString(), anyString(), any(LocalDate.class)))
            .thenReturn(successResult);

        // When
        pantryController.showMenu(user);

        // Then
        assertThat(outContent.toString()).contains("请输入您的选择");
        verify(pantryService).addPantryItem(anyInt(), anyString(), anyString(), any(LocalDate.class));
    }

    @Test
    void testShowMenuWithInvalidChoice() {
        // Given
        User user = createUser(1, "testuser");
        String input = "99\n0\n"; // 无效选择然后退出
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        // When
        pantryController.showMenu(user);

        // Then
        assertThat(outContent.toString()).contains("无效的选择，请重新输入！");
    }

    @Test
    void testAddPantryItemSuccess() {
        // Given
        User user = createUser(1, "testuser");
        String input = "鸡蛋\n10个\n2024-12-31\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        PantryService.PantryResult successResult = new PantryService.PantryResult(true, null, "添加成功");
        when(pantryService.addPantryItem(eq(1), eq("鸡蛋"), eq("10个"), any(LocalDate.class)))
            .thenReturn(successResult);

        // When
        invokePrivateMethod(pantryController, "addPantryItem", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("添加食品到库存");
        assertThat(output).contains("食品添加成功！");
        verify(pantryService).addPantryItem(eq(1), eq("鸡蛋"), eq("10个"), any(LocalDate.class));
    }

    @Test
    void testAddPantryItemInvalidDateFormat() {
        // Given
        User user = createUser(1, "testuser");
        String input = "鸡蛋\n10个\ninvalid-date\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        // When
        invokePrivateMethod(pantryController, "addPantryItem", user);

        // Then
        assertThat(outContent.toString()).contains("输入格式有误，请检查！");
        verify(pantryService, never()).addPantryItem(anyInt(), anyString(), anyString(), any(LocalDate.class));
    }

    @Test
    void testAddPantryItemFailure() {
        // Given
        User user = createUser(1, "testuser");
        String input = "鸡蛋\n10个\n2024-12-31\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        PantryService.PantryResult failureResult = new PantryService.PantryResult(false, null, "添加失败");
        when(pantryService.addPantryItem(eq(1), eq("鸡蛋"), eq("10个"), any(LocalDate.class)))
            .thenReturn(failureResult);

        // When
        invokePrivateMethod(pantryController, "addPantryItem", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("添加失败");
        verify(pantryService).addPantryItem(eq(1), eq("鸡蛋"), eq("10个"), any(LocalDate.class));
    }

    @Test
    void testViewPantryWithItems() {
        // Given
        User user = createUser(1, "testuser");
        List<PantryItem> items = Arrays.asList(createPantryItem(1, 1, 1, "10个", LocalDate.now().plusDays(7)));
        when(pantryService.getPantryItemsByUser(1)).thenReturn(items);

        // When
        invokePrivateMethod(pantryController, "viewPantry", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("当前库存");
        assertThat(output).contains("您的库存共有 1 件物品");
        verify(pantryService).getPantryItemsByUser(1);
    }

    @Test
    void testViewPantryEmpty() {
        // Given
        User user = createUser(1, "testuser");
        when(pantryService.getPantryItemsByUser(1)).thenReturn(Arrays.asList());

        // When
        invokePrivateMethod(pantryController, "viewPantry", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("当前库存");
        assertThat(output).contains("您的库存为空");
        verify(pantryService).getPantryItemsByUser(1);
    }

    @Test
    void testUpdatePantryItemSuccess() {
        // Given
        User user = createUser(1, "testuser");
        List<PantryItem> items = Arrays.asList(createPantryItem(1, 1, 1, "10个", LocalDate.now().plusDays(7)));
        when(pantryService.getPantryItemsByUser(1)).thenReturn(items);
        
        String input = "1\n20个\n2024-12-31\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        PantryService.PantryResult successResult = new PantryService.PantryResult(true, null, "更新成功");
        when(pantryService.updatePantryItem(eq(1), eq(1), eq("20个"), any(LocalDate.class)))
            .thenReturn(successResult);

        // When
        invokePrivateMethod(pantryController, "updatePantryItem", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("更新食品信息");
        assertThat(output).contains("库存信息更新成功");
        verify(pantryService).updatePantryItem(eq(1), eq(1), eq("20个"), any(LocalDate.class));
    }

    @Test
    void testUpdatePantryItemEmptyList() {
        // Given
        User user = createUser(1, "testuser");
        when(pantryService.getPantryItemsByUser(1)).thenReturn(Arrays.asList());

        // When
        invokePrivateMethod(pantryController, "updatePantryItem", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("更新食品信息");
        assertThat(output).contains("您的库存为空，无法更新");
        verify(pantryService, never()).updatePantryItem(anyInt(), anyInt(), anyString(), any(LocalDate.class));
    }

    @Test
    void testUpdatePantryItemInvalidChoice() {
        // Given
        User user = createUser(1, "testuser");
        List<PantryItem> items = Arrays.asList(createPantryItem(1, 1, 1, "10个", LocalDate.now().plusDays(7)));
        when(pantryService.getPantryItemsByUser(1)).thenReturn(items);
        
        String input = "99\n"; // 无效选择
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        // When
        invokePrivateMethod(pantryController, "updatePantryItem", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("无效的选择");
        verify(pantryService, never()).updatePantryItem(anyInt(), anyInt(), anyString(), any(LocalDate.class));
    }

    @Test
    void testDeletePantryItemSuccess() {
        // Given
        User user = createUser(1, "testuser");
        List<PantryItem> items = Arrays.asList(createPantryItem(1, 1, 1, "10个", LocalDate.now().plusDays(7)));
        when(pantryService.getPantryItemsByUser(1)).thenReturn(items);
        
        String input = "1\ny\n"; // 选择删除并确认
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        PantryService.PantryResult successResult = new PantryService.PantryResult(true, null, "删除成功");
        when(pantryService.deletePantryItem(eq(1), eq(1))).thenReturn(successResult);

        // When
        invokePrivateMethod(pantryController, "deletePantryItem", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("删除库存食品");
        assertThat(output).contains("食品删除成功");
        verify(pantryService).deletePantryItem(eq(1), eq(1));
    }

    @Test
    void testDeletePantryItemCancelled() {
        // Given
        User user = createUser(1, "testuser");
        List<PantryItem> items = Arrays.asList(createPantryItem(1, 1, 1, "10个", LocalDate.now().plusDays(7)));
        when(pantryService.getPantryItemsByUser(1)).thenReturn(items);
        
        String input = "1\nn\n"; // 选择删除但取消
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(sessionManager.getScanner()).thenReturn(mockScanner);

        // When
        invokePrivateMethod(pantryController, "deletePantryItem", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("删除库存食品");
        assertThat(output).contains("已取消删除操作");
        verify(pantryService, never()).deletePantryItem(anyInt(), anyInt());
    }

    @Test
    void testCheckExpiringItems() {
        // Given
        User user = createUser(1, "testuser");
        List<PantryItem> items = Arrays.asList(createPantryItem(1, 1, 1, "10个", LocalDate.now().plusDays(3)));
        when(pantryService.getExpiringItems(1, 7)).thenReturn(items);

        // When
        invokePrivateMethod(pantryController, "checkExpiringItems", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("即将过期食品");
        assertThat(output).contains("以下食品即将过期");
        verify(pantryService).getExpiringItems(1, 7);
    }

    @Test
    void testCheckExpiringItemsEmpty() {
        // Given
        User user = createUser(1, "testuser");
        when(pantryService.getExpiringItems(1, 7)).thenReturn(Arrays.asList());

        // When
        invokePrivateMethod(pantryController, "checkExpiringItems", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("即将过期食品");
        assertThat(output).contains("近期没有即将过期的食品");
        verify(pantryService).getExpiringItems(1, 7);
    }

    @Test
    void testViewExpiredItems() {
        // Given
        User user = createUser(1, "testuser");
        List<PantryItem> items = Arrays.asList(createPantryItem(1, 1, 1, "10个", LocalDate.now().minusDays(2)));
        when(pantryService.getExpiredItems(1)).thenReturn(items);

        // When
        invokePrivateMethod(pantryController, "viewExpiredItems", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("已过期食品");
        assertThat(output).contains("以下食品已过期");
        verify(pantryService).getExpiredItems(1);
    }

    @Test
    void testViewExpiredItemsEmpty() {
        // Given
        User user = createUser(1, "testuser");
        when(pantryService.getExpiredItems(1)).thenReturn(Arrays.asList());

        // When
        invokePrivateMethod(pantryController, "viewExpiredItems", user);

        // Then
        String output = outContent.toString();
        assertThat(output).contains("已过期食品");
        assertThat(output).contains("没有已过期的食品");
        verify(pantryService).getExpiredItems(1);
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

    private User createUser(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private PantryItem createPantryItem(int id, int userId, int ingredientId, String quantity, LocalDate expiryDate) {
        PantryItem item = new PantryItem();
        item.setId(id);
        item.setUserId(userId);
        item.setIngredientId(ingredientId);
        item.setQuantity(quantity);
        item.setExpiryDate(expiryDate);
        
        // 设置食材信息
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        ingredient.setName("测试食材");
        item.setIngredient(ingredient);
        
        return item;
    }
}