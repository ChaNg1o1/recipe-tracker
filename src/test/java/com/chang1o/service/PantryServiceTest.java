package com.chang1o.service;

import com.chang1o.dao.PantryDao;
import com.chang1o.dao.IngredientDao;
import com.chang1o.model.PantryItem;
import com.chang1o.model.Ingredient;
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
class PantryServiceTest {

    @Mock
    private PantryDao pantryDao;

    @Mock
    private IngredientDao ingredientDao;

    private PantryService pantryService;

    @BeforeEach
    void setUp() {
        pantryService = new PantryService();
        injectMocks();
    }

    private void injectMocks() {
        try {
            java.lang.reflect.Field field = PantryService.class.getDeclaredField("pantryDao");
            field.setAccessible(true);
            field.set(pantryService, pantryDao);
            
            field = PantryService.class.getDeclaredField("ingredientDao");
            field.setAccessible(true);
            field.set(pantryService, ingredientDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PantryItem createPantryItem(int id, int userId, int ingredientId, String quantity, LocalDate expiryDate) {
        PantryItem item = new PantryItem();
        item.setId(id);
        item.setUserId(userId);
        item.setIngredientId(ingredientId);
        item.setQuantity(quantity);
        item.setExpiryDate(expiryDate);
        return item;
    }

    private Ingredient createIngredient(int id, String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        return ingredient;
    }

    @Test
    void testAddPantryItemSuccess() {
        // Given
        int userId = 1;
        String ingredientName = "鸡蛋";
        String quantity = "10个";
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        Ingredient existingIngredient = createIngredient(1, ingredientName);
        when(ingredientDao.getIngredientByName(ingredientName)).thenReturn(existingIngredient);
        when(pantryDao.addPantryItem(any(PantryItem.class))).thenReturn(true);

        // Remove unnecessary stubbing - already stubbed above

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("库存物品添加成功！");
        verify(ingredientDao).getIngredientByName(ingredientName);
        verify(pantryDao).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemNewIngredient() {
        // Given
        int userId = 1;
        String ingredientName = "新原料";
        String quantity = "5斤";
        LocalDate expiryDate = LocalDate.now().plusDays(15);

        when(ingredientDao.getIngredientByName(ingredientName)).thenReturn(null);
        
        Ingredient newIngredient = createIngredient(2, ingredientName);
        when(ingredientDao.addIngredient(any(Ingredient.class))).thenReturn(true);
        newIngredient.setId(2);

        PantryItem savedItem = createPantryItem(1, userId, 2, quantity, expiryDate);
        when(pantryDao.addPantryItem(any(PantryItem.class))).thenReturn(true);
        savedItem.setIngredient(newIngredient);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("库存物品添加成功！");
        verify(ingredientDao).getIngredientByName(ingredientName);
        verify(ingredientDao).addIngredient(any(Ingredient.class));
        verify(pantryDao).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemInvalidIngredientName() {
        // Given
        int userId = 1;
        String ingredientName = ""; // 无效名称
        String quantity = "5斤";
        LocalDate expiryDate = LocalDate.now().plusDays(15);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("原料名称不能为空");
        verify(ingredientDao, never()).getIngredientByName(anyString());
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemIngredientNameTooLong() {
        // Given
        int userId = 1;
        String ingredientName = "a".repeat(101); // 101 characters, above maximum of 100
        String quantity = "5斤";
        LocalDate expiryDate = LocalDate.now().plusDays(15);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("原料名称长度必须在1-100个字符之间");
        verify(ingredientDao, never()).getIngredientByName(anyString());
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemEmptyQuantity() {
        // Given
        int userId = 1;
        String ingredientName = "鸡蛋";
        String quantity = ""; // 空数量
        LocalDate expiryDate = LocalDate.now().plusDays(15);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("数量不能为空");
        verify(ingredientDao, never()).getIngredientByName(anyString());
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemQuantityTooLong() {
        // Given
        int userId = 1;
        String ingredientName = "鸡蛋";
        String quantity = "a".repeat(51); // 51 characters, above maximum of 50
        LocalDate expiryDate = LocalDate.now().plusDays(15);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("数量描述太长，最多50个字符");
        verify(ingredientDao, never()).getIngredientByName(anyString());
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemAddIngredientFailure() {
        // Given
        int userId = 1;
        String ingredientName = "新原料";
        String quantity = "5斤";
        LocalDate expiryDate = LocalDate.now().plusDays(15);

        when(ingredientDao.getIngredientByName(ingredientName)).thenReturn(null);
        when(ingredientDao.addIngredient(any(Ingredient.class))).thenReturn(false);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("添加原料失败，请稍后重试");
        verify(ingredientDao).getIngredientByName(ingredientName);
        verify(ingredientDao).addIngredient(any(Ingredient.class));
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemDatabaseFailure() {
        // Given
        int userId = 1;
        String ingredientName = "鸡蛋";
        String quantity = "10个";
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        Ingredient existingIngredient = createIngredient(1, ingredientName);
        when(ingredientDao.getIngredientByName(ingredientName)).thenReturn(existingIngredient);
        when(pantryDao.addPantryItem(any(PantryItem.class))).thenReturn(false);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("库存物品添加失败，请稍后重试");
        verify(ingredientDao).getIngredientByName(ingredientName);
        verify(pantryDao).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testUpdatePantryItemSuccess() {
        // Given
        int itemId = 1;
        int userId = 1;
        String newQuantity = "20个";
        LocalDate newExpiryDate = LocalDate.now().plusDays(45);

        PantryItem existingItem = createPantryItem(itemId, userId, 1, "10个", LocalDate.now().plusDays(30));
        when(pantryDao.getPantryItemById(itemId)).thenReturn(existingItem);
        when(pantryDao.updatePantryItem(any(PantryItem.class))).thenReturn(true);

        Ingredient ingredient = createIngredient(1, "鸡蛋");
        when(ingredientDao.getIngredientById(1)).thenReturn(ingredient);

        // When
        PantryService.PantryResult result = pantryService.updatePantryItem(itemId, userId, newQuantity, newExpiryDate);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getQuantity()).isEqualTo(newQuantity);
        assertThat(result.getMessage()).isEqualTo("库存物品更新成功！");
        verify(pantryDao).getPantryItemById(itemId);
        verify(pantryDao).updatePantryItem(any(PantryItem.class));
    }

    @Test
    void testUpdatePantryItemNotFound() {
        // Given
        int itemId = 999;
        int userId = 1;
        String newQuantity = "20个";
        LocalDate newExpiryDate = LocalDate.now().plusDays(45);

        when(pantryDao.getPantryItemById(itemId)).thenReturn(null);

        // When
        PantryService.PantryResult result = pantryService.updatePantryItem(itemId, userId, newQuantity, newExpiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("库存物品不存在");
        verify(pantryDao).getPantryItemById(itemId);
        verify(pantryDao, never()).updatePantryItem(any(PantryItem.class));
    }

    @Test
    void testUpdatePantryItemUnauthorized() {
        // Given
        int itemId = 1;
        int userId = 2; // 不同的用户ID
        String newQuantity = "20个";
        LocalDate newExpiryDate = LocalDate.now().plusDays(45);

        PantryItem existingItem = createPantryItem(itemId, 1, 1, "10个", LocalDate.now().plusDays(30));
        when(pantryDao.getPantryItemById(itemId)).thenReturn(existingItem);

        // When
        PantryService.PantryResult result = pantryService.updatePantryItem(itemId, userId, newQuantity, newExpiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("您没有权限修改这个库存物品");
        verify(pantryDao).getPantryItemById(itemId);
        verify(pantryDao, never()).updatePantryItem(any(PantryItem.class));
    }

    @Test
    void testUpdatePantryItemInvalidQuantity() {
        // Given
        int itemId = 1;
        int userId = 1;
        String newQuantity = ""; // 空数量
        LocalDate newExpiryDate = LocalDate.now().plusDays(45);

        // When
        PantryService.PantryResult result = pantryService.updatePantryItem(itemId, userId, newQuantity, newExpiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("数量不能为空");
        verify(pantryDao, never()).getPantryItemById(anyInt());
        verify(pantryDao, never()).updatePantryItem(any(PantryItem.class));
    }

    @Test
    void testUpdatePantryItemDatabaseFailure() {
        // Given
        int itemId = 1;
        int userId = 1;
        String newQuantity = "20个";
        LocalDate newExpiryDate = LocalDate.now().plusDays(45);

        PantryItem existingItem = createPantryItem(itemId, userId, 1, "10个", LocalDate.now().plusDays(30));
        when(pantryDao.getPantryItemById(itemId)).thenReturn(existingItem);
        when(pantryDao.updatePantryItem(any(PantryItem.class))).thenReturn(false);

        // When
        PantryService.PantryResult result = pantryService.updatePantryItem(itemId, userId, newQuantity, newExpiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("库存物品更新失败，请稍后重试");
        verify(pantryDao).getPantryItemById(itemId);
        verify(pantryDao).updatePantryItem(any(PantryItem.class));
    }

    @Test
    void testDeletePantryItemSuccess() {
        // Given
        int itemId = 1;
        int userId = 1;

        PantryItem existingItem = createPantryItem(itemId, userId, 1, "10个", LocalDate.now().plusDays(30));
        when(pantryDao.getPantryItemById(itemId)).thenReturn(existingItem);
        when(pantryDao.deletePantryItem(itemId)).thenReturn(true);

        // When
        PantryService.PantryResult result = pantryService.deletePantryItem(itemId, userId);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("库存物品删除成功！");
        verify(pantryDao).getPantryItemById(itemId);
        verify(pantryDao).deletePantryItem(itemId);
    }

    @Test
    void testDeletePantryItemNotFound() {
        // Given
        int itemId = 999;
        int userId = 1;

        when(pantryDao.getPantryItemById(itemId)).thenReturn(null);

        // When
        PantryService.PantryResult result = pantryService.deletePantryItem(itemId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("库存物品不存在");
        verify(pantryDao).getPantryItemById(itemId);
        verify(pantryDao, never()).deletePantryItem(itemId);
    }

    @Test
    void testDeletePantryItemUnauthorized() {
        // Given
        int itemId = 1;
        int userId = 2; // 不同的用户ID

        PantryItem existingItem = createPantryItem(itemId, 1, 1, "10个", LocalDate.now().plusDays(30));
        when(pantryDao.getPantryItemById(itemId)).thenReturn(existingItem);

        // When
        PantryService.PantryResult result = pantryService.deletePantryItem(itemId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("您没有权限删除这个库存物品");
        verify(pantryDao).getPantryItemById(itemId);
        verify(pantryDao, never()).deletePantryItem(itemId);
    }

    @Test
    void testDeletePantryItemDatabaseFailure() {
        // Given
        int itemId = 1;
        int userId = 1;

        PantryItem existingItem = createPantryItem(itemId, userId, 1, "10个", LocalDate.now().plusDays(30));
        when(pantryDao.getPantryItemById(itemId)).thenReturn(existingItem);
        when(pantryDao.deletePantryItem(itemId)).thenReturn(false);

        // When
        PantryService.PantryResult result = pantryService.deletePantryItem(itemId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("库存物品删除失败，请稍后重试");
        verify(pantryDao).getPantryItemById(itemId);
        verify(pantryDao).deletePantryItem(itemId);
    }

    @Test
    void testGetPantryItemByIdSuccess() {
        // Given
        int itemId = 1;
        PantryItem item = createPantryItem(itemId, 1, 1, "10个", LocalDate.now().plusDays(30));
        
        Ingredient ingredient = createIngredient(1, "鸡蛋");
        when(pantryDao.getPantryItemById(itemId)).thenReturn(item);
        when(ingredientDao.getIngredientById(1)).thenReturn(ingredient);

        // When
        PantryItem result = pantryService.getPantryItemById(itemId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(itemId);
        assertThat(result.getIngredient()).isEqualTo(ingredient);
        verify(pantryDao).getPantryItemById(itemId);
        verify(ingredientDao).getIngredientById(1);
    }

    @Test
    void testGetPantryItemByIdNotFound() {
        // Given
        int itemId = 999;
        when(pantryDao.getPantryItemById(itemId)).thenReturn(null);

        // When
        PantryItem result = pantryService.getPantryItemById(itemId);

        // Then
        assertThat(result).isNull();
        verify(pantryDao).getPantryItemById(itemId);
        verify(ingredientDao, never()).getIngredientById(anyInt());
    }

    @Test
    void testGetPantryItemsByUser() {
        // Given
        int userId = 1;
        List<PantryItem> items = Arrays.asList(
            createPantryItem(1, userId, 1, "10个", LocalDate.now().plusDays(30)),
            createPantryItem(2, userId, 2, "5斤", LocalDate.now().plusDays(15))
        );

        Ingredient ingredient1 = createIngredient(1, "鸡蛋");
        Ingredient ingredient2 = createIngredient(2, "大米");
        
        when(pantryDao.getPantryItemsByUser(userId)).thenReturn(items);
        when(ingredientDao.getIngredientById(1)).thenReturn(ingredient1);
        when(ingredientDao.getIngredientById(2)).thenReturn(ingredient2);

        // When
        List<PantryItem> result = pantryService.getPantryItemsByUser(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getIngredient()).isEqualTo(ingredient1);
        assertThat(result.get(1).getIngredient()).isEqualTo(ingredient2);
        verify(pantryDao).getPantryItemsByUser(userId);
        verify(ingredientDao).getIngredientById(1);
        verify(ingredientDao).getIngredientById(2);
    }

    @Test
    void testGetExpiringItems() {
        // Given
        int userId = 1;
        int days = 7;
        List<PantryItem> items = Arrays.asList(
            createPantryItem(1, userId, 1, "10个", LocalDate.now().plusDays(5))
        );

        Ingredient ingredient = createIngredient(1, "鸡蛋");
        when(pantryDao.getExpiringItems(userId, days)).thenReturn(items);
        when(ingredientDao.getIngredientById(1)).thenReturn(ingredient);

        // When
        List<PantryItem> result = pantryService.getExpiringItems(userId, days);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getIngredient()).isEqualTo(ingredient);
        verify(pantryDao).getExpiringItems(userId, days);
        verify(ingredientDao).getIngredientById(1);
    }

    @Test
    void testGetExpiredItems() {
        // Given
        int userId = 1;
        List<PantryItem> items = Arrays.asList(
            createPantryItem(1, userId, 1, "10个", LocalDate.now().minusDays(1))
        );

        Ingredient ingredient = createIngredient(1, "鸡蛋");
        when(pantryDao.getExpiredItems(userId)).thenReturn(items);
        when(ingredientDao.getIngredientById(1)).thenReturn(ingredient);

        // When
        List<PantryItem> result = pantryService.getExpiredItems(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getIngredient()).isEqualTo(ingredient);
        verify(pantryDao).getExpiredItems(userId);
        verify(ingredientDao).getIngredientById(1);
    }

    @Test
    void testHasExpiringItemsWithExpiringItems() {
        // Given
        int userId = 1;
        int days = 7;
        List<PantryItem> items = Arrays.asList(
            createPantryItem(1, userId, 1, "10个", LocalDate.now().plusDays(5))
        );

        when(pantryDao.getExpiringItems(userId, days)).thenReturn(items);

        // When
        boolean result = pantryService.hasExpiringItems(userId, days);

        // Then
        assertThat(result).isTrue();
        verify(pantryDao).getExpiringItems(userId, days);
    }

    @Test
    void testHasExpiringItemsWithoutExpiringItems() {
        // Given
        int userId = 1;
        int days = 7;
        List<PantryItem> items = List.of();

        when(pantryDao.getExpiringItems(userId, days)).thenReturn(items);

        // When
        boolean result = pantryService.hasExpiringItems(userId, days);

        // Then
        assertThat(result).isFalse();
        verify(pantryDao).getExpiringItems(userId, days);
    }

    @Test
    void testValidatePantryInputValid() {
        // Given
        String ingredientName = "鸡蛋";
        String quantity = "10个";

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(1, ingredientName, quantity, LocalDate.now().plusDays(30));

        // Then
        // 验证通过的情况应该到达数据库调用阶段
        assertThat(result.isSuccess() || result.getMessage().contains("添加失败") || result.getMessage().contains("添加原料失败")).isTrue();
    }

    @Test
    void testValidatePantryInputInvalidName() {
        // When
        PantryService.PantryResult result = pantryService.addPantryItem(1, "", "有效数量", LocalDate.now().plusDays(30));

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("原料名称不能为空");
    }

    @Test
    void testValidatePantryInputInvalidQuantity() {
        // When
        PantryService.PantryResult result = pantryService.addPantryItem(1, "有效名称", "", LocalDate.now().plusDays(30));

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("数量不能为空");
    }

    // Additional boundary condition tests for Task 3.5

    @Test
    void testAddPantryItemNullIngredientName() {
        // Given
        int userId = 1;
        String ingredientName = null;
        String quantity = "10个";
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("原料名称不能为空");
        verify(ingredientDao, never()).getIngredientByName(anyString());
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemWhitespaceOnlyIngredientName() {
        // Given
        int userId = 1;
        String ingredientName = "   "; // Only whitespace
        String quantity = "10个";
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("原料名称不能为空");
        verify(ingredientDao, never()).getIngredientByName(anyString());
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemIngredientNameMinimumLength() {
        // Given
        int userId = 1;
        String ingredientName = "a"; // 1 character, minimum valid length
        String quantity = "10个";
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        Ingredient existingIngredient = createIngredient(1, ingredientName);
        when(ingredientDao.getIngredientByName(ingredientName)).thenReturn(existingIngredient);
        when(pantryDao.addPantryItem(any(PantryItem.class))).thenReturn(true);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("库存物品添加成功！");
        verify(ingredientDao).getIngredientByName(ingredientName);
        verify(pantryDao).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemIngredientNameMaximumLength() {
        // Given
        int userId = 1;
        String ingredientName = "a".repeat(100); // 100 characters, maximum valid length
        String quantity = "10个";
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        Ingredient existingIngredient = createIngredient(1, ingredientName);
        when(ingredientDao.getIngredientByName(ingredientName)).thenReturn(existingIngredient);
        when(pantryDao.addPantryItem(any(PantryItem.class))).thenReturn(true);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("库存物品添加成功！");
        verify(ingredientDao).getIngredientByName(ingredientName);
        verify(pantryDao).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemIngredientNameTooLongExact() {
        // Given
        int userId = 1;
        String ingredientName = "a".repeat(101); // 101 characters, above maximum of 100
        String quantity = "10个";
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("原料名称长度必须在1-100个字符之间");
        verify(ingredientDao, never()).getIngredientByName(anyString());
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemNullQuantity() {
        // Given
        int userId = 1;
        String ingredientName = "鸡蛋";
        String quantity = null;
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("数量不能为空");
        verify(ingredientDao, never()).getIngredientByName(anyString());
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemWhitespaceOnlyQuantity() {
        // Given
        int userId = 1;
        String ingredientName = "鸡蛋";
        String quantity = "   "; // Only whitespace
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("数量不能为空");
        verify(ingredientDao, never()).getIngredientByName(anyString());
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemQuantityMaximumLength() {
        // Given
        int userId = 1;
        String ingredientName = "鸡蛋";
        String quantity = "a".repeat(50); // 50 characters, maximum valid length
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        Ingredient existingIngredient = createIngredient(1, ingredientName);
        when(ingredientDao.getIngredientByName(ingredientName)).thenReturn(existingIngredient);
        when(pantryDao.addPantryItem(any(PantryItem.class))).thenReturn(true);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("库存物品添加成功！");
        verify(ingredientDao).getIngredientByName(ingredientName);
        verify(pantryDao).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testAddPantryItemQuantityTooLongExact() {
        // Given
        int userId = 1;
        String ingredientName = "鸡蛋";
        String quantity = "a".repeat(51); // 51 characters, above maximum of 50
        LocalDate expiryDate = LocalDate.now().plusDays(30);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("数量描述太长，最多50个字符");
        verify(ingredientDao, never()).getIngredientByName(anyString());
        verify(pantryDao, never()).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testUpdatePantryItemNullQuantity() {
        // Given
        int itemId = 1;
        int userId = 1;
        String newQuantity = null;
        LocalDate newExpiryDate = LocalDate.now().plusDays(45);

        // When
        PantryService.PantryResult result = pantryService.updatePantryItem(itemId, userId, newQuantity, newExpiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("数量不能为空");
        verify(pantryDao, never()).getPantryItemById(anyInt());
        verify(pantryDao, never()).updatePantryItem(any(PantryItem.class));
    }

    @Test
    void testUpdatePantryItemWhitespaceOnlyQuantity() {
        // Given
        int itemId = 1;
        int userId = 1;
        String newQuantity = "   "; // Only whitespace
        LocalDate newExpiryDate = LocalDate.now().plusDays(45);

        // When
        PantryService.PantryResult result = pantryService.updatePantryItem(itemId, userId, newQuantity, newExpiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("数量不能为空");
        verify(pantryDao, never()).getPantryItemById(anyInt());
        verify(pantryDao, never()).updatePantryItem(any(PantryItem.class));
    }

    @Test
    void testUpdatePantryItemQuantityTooLong() {
        // Given
        int itemId = 1;
        int userId = 1;
        String newQuantity = "a".repeat(51); // 51 characters, above maximum of 50
        LocalDate newExpiryDate = LocalDate.now().plusDays(45);

        // When
        PantryService.PantryResult result = pantryService.updatePantryItem(itemId, userId, newQuantity, newExpiryDate);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getItem()).isNull();
        assertThat(result.getMessage()).isEqualTo("数量描述太长，最多50个字符");
        verify(pantryDao, never()).getPantryItemById(anyInt());
        verify(pantryDao, never()).updatePantryItem(any(PantryItem.class));
    }

    @Test
    void testGetPantryItemsByUserEmptyResult() {
        // Given
        int userId = 999; // User with no pantry items
        List<PantryItem> emptyItems = Arrays.asList();

        when(pantryDao.getPantryItemsByUser(userId)).thenReturn(emptyItems);

        // When
        List<PantryItem> result = pantryService.getPantryItemsByUser(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(pantryDao).getPantryItemsByUser(userId);
        verify(ingredientDao, never()).getIngredientById(anyInt());
    }

    @Test
    void testGetExpiringItemsEmptyResult() {
        // Given
        int userId = 1;
        int days = 7;
        List<PantryItem> emptyItems = Arrays.asList();

        when(pantryDao.getExpiringItems(userId, days)).thenReturn(emptyItems);

        // When
        List<PantryItem> result = pantryService.getExpiringItems(userId, days);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(pantryDao).getExpiringItems(userId, days);
        verify(ingredientDao, never()).getIngredientById(anyInt());
    }

    @Test
    void testGetExpiredItemsEmptyResult() {
        // Given
        int userId = 1;
        List<PantryItem> emptyItems = Arrays.asList();

        when(pantryDao.getExpiredItems(userId)).thenReturn(emptyItems);

        // When
        List<PantryItem> result = pantryService.getExpiredItems(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(pantryDao).getExpiredItems(userId);
        verify(ingredientDao, never()).getIngredientById(anyInt());
    }

    @Test
    void testGetExpiringItemsWithZeroDays() {
        // Given
        int userId = 1;
        int days = 0; // Items expiring today
        List<PantryItem> items = Arrays.asList(
            createPantryItem(1, userId, 1, "10个", LocalDate.now())
        );

        Ingredient ingredient = createIngredient(1, "鸡蛋");
        when(pantryDao.getExpiringItems(userId, days)).thenReturn(items);
        when(ingredientDao.getIngredientById(1)).thenReturn(ingredient);

        // When
        List<PantryItem> result = pantryService.getExpiringItems(userId, days);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getIngredient()).isEqualTo(ingredient);
        verify(pantryDao).getExpiringItems(userId, days);
        verify(ingredientDao).getIngredientById(1);
    }

    @Test
    void testGetExpiringItemsWithNegativeDays() {
        // Given
        int userId = 1;
        int days = -1; // Negative days (edge case)
        List<PantryItem> items = Arrays.asList();

        when(pantryDao.getExpiringItems(userId, days)).thenReturn(items);

        // When
        List<PantryItem> result = pantryService.getExpiringItems(userId, days);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(pantryDao).getExpiringItems(userId, days);
    }

    @Test
    void testHasExpiringItemsWithZeroDays() {
        // Given
        int userId = 1;
        int days = 0;
        List<PantryItem> items = Arrays.asList(
            createPantryItem(1, userId, 1, "10个", LocalDate.now())
        );

        when(pantryDao.getExpiringItems(userId, days)).thenReturn(items);

        // When
        boolean result = pantryService.hasExpiringItems(userId, days);

        // Then
        assertThat(result).isTrue();
        verify(pantryDao).getExpiringItems(userId, days);
    }

    @Test
    void testAddPantryItemWithPastExpiryDate() {
        // Given
        int userId = 1;
        String ingredientName = "鸡蛋";
        String quantity = "10个";
        LocalDate expiryDate = LocalDate.now().minusDays(1); // Past date

        Ingredient existingIngredient = createIngredient(1, ingredientName);
        when(ingredientDao.getIngredientByName(ingredientName)).thenReturn(existingIngredient);
        when(pantryDao.addPantryItem(any(PantryItem.class))).thenReturn(true);

        // When
        PantryService.PantryResult result = pantryService.addPantryItem(userId, ingredientName, quantity, expiryDate);

        // Then
        assertThat(result.isSuccess()).isTrue(); // Service allows past dates
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("库存物品添加成功！");
        verify(ingredientDao).getIngredientByName(ingredientName);
        verify(pantryDao).addPantryItem(any(PantryItem.class));
    }

    @Test
    void testUpdatePantryItemWithPastExpiryDate() {
        // Given
        int itemId = 1;
        int userId = 1;
        String newQuantity = "20个";
        LocalDate newExpiryDate = LocalDate.now().minusDays(1); // Past date

        PantryItem existingItem = createPantryItem(itemId, userId, 1, "10个", LocalDate.now().plusDays(30));
        when(pantryDao.getPantryItemById(itemId)).thenReturn(existingItem);
        when(pantryDao.updatePantryItem(any(PantryItem.class))).thenReturn(true);

        Ingredient ingredient = createIngredient(1, "鸡蛋");
        when(ingredientDao.getIngredientById(1)).thenReturn(ingredient);

        // When
        PantryService.PantryResult result = pantryService.updatePantryItem(itemId, userId, newQuantity, newExpiryDate);

        // Then
        assertThat(result.isSuccess()).isTrue(); // Service allows past dates
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getQuantity()).isEqualTo(newQuantity);
        assertThat(result.getItem().getExpiryDate()).isEqualTo(newExpiryDate);
        assertThat(result.getMessage()).isEqualTo("库存物品更新成功！");
        verify(pantryDao).getPantryItemById(itemId);
        verify(pantryDao).updatePantryItem(any(PantryItem.class));
    }

    @Test
    void testGetExpiringItemsMultipleItems() {
        // Given
        int userId = 1;
        int days = 7;
        List<PantryItem> items = Arrays.asList(
            createPantryItem(1, userId, 1, "10个", LocalDate.now().plusDays(3)),
            createPantryItem(2, userId, 2, "5斤", LocalDate.now().plusDays(6)),
            createPantryItem(3, userId, 3, "2瓶", LocalDate.now().plusDays(7))
        );

        Ingredient ingredient1 = createIngredient(1, "鸡蛋");
        Ingredient ingredient2 = createIngredient(2, "大米");
        Ingredient ingredient3 = createIngredient(3, "酱油");
        
        when(pantryDao.getExpiringItems(userId, days)).thenReturn(items);
        when(ingredientDao.getIngredientById(1)).thenReturn(ingredient1);
        when(ingredientDao.getIngredientById(2)).thenReturn(ingredient2);
        when(ingredientDao.getIngredientById(3)).thenReturn(ingredient3);

        // When
        List<PantryItem> result = pantryService.getExpiringItems(userId, days);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getIngredient()).isEqualTo(ingredient1);
        assertThat(result.get(1).getIngredient()).isEqualTo(ingredient2);
        assertThat(result.get(2).getIngredient()).isEqualTo(ingredient3);
        verify(pantryDao).getExpiringItems(userId, days);
        verify(ingredientDao).getIngredientById(1);
        verify(ingredientDao).getIngredientById(2);
        verify(ingredientDao).getIngredientById(3);
    }

    @Test
    void testGetExpiredItemsMultipleItems() {
        // Given
        int userId = 1;
        List<PantryItem> items = Arrays.asList(
            createPantryItem(1, userId, 1, "10个", LocalDate.now().minusDays(1)),
            createPantryItem(2, userId, 2, "5斤", LocalDate.now().minusDays(5))
        );

        Ingredient ingredient1 = createIngredient(1, "鸡蛋");
        Ingredient ingredient2 = createIngredient(2, "大米");
        
        when(pantryDao.getExpiredItems(userId)).thenReturn(items);
        when(ingredientDao.getIngredientById(1)).thenReturn(ingredient1);
        when(ingredientDao.getIngredientById(2)).thenReturn(ingredient2);

        // When
        List<PantryItem> result = pantryService.getExpiredItems(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getIngredient()).isEqualTo(ingredient1);
        assertThat(result.get(1).getIngredient()).isEqualTo(ingredient2);
        verify(pantryDao).getExpiredItems(userId);
        verify(ingredientDao).getIngredientById(1);
        verify(ingredientDao).getIngredientById(2);
    }
}
