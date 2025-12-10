package com.chang1o.service;

import com.chang1o.dao.RecipeDao;
import com.chang1o.dao.CategoryDao;
import com.chang1o.dao.IngredientDao;
import com.chang1o.dao.RecipeIngredientDao;
import com.chang1o.model.Recipe;
import com.chang1o.model.Category;
import com.chang1o.model.RecipeIngredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeDao recipeDao;

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private IngredientDao ingredientDao;

    @Mock
    private RecipeIngredientDao recipeIngredientDao;

    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService();
        injectMocks();
    }

    private void injectMocks() {
        try {
            java.lang.reflect.Field field = RecipeService.class.getDeclaredField("recipeDao");
            field.setAccessible(true);
            field.set(recipeService, recipeDao);
            
            field = RecipeService.class.getDeclaredField("categoryDao");
            field.setAccessible(true);
            field.set(recipeService, categoryDao);
            
            field = RecipeService.class.getDeclaredField("ingredientDao");
            field.setAccessible(true);
            field.set(recipeService, ingredientDao);
            
            field = RecipeService.class.getDeclaredField("recipeIngredientDao");
            field.setAccessible(true);
            field.set(recipeService, recipeIngredientDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Recipe createRecipe(int id, String name, String instructions, int categoryId, int userId) {
        Recipe recipe = new Recipe(name, instructions, categoryId, userId);
        recipe.setId(id);
        return recipe;
    }

    private RecipeIngredient createRecipeIngredient(int id, int recipeId, int ingredientId, String quantity) {
        return new RecipeIngredient(recipeId, ingredientId, quantity);
    }

    @Test
    void testAddRecipeSuccess() {
        // Given
        String name = "红烧肉";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;

        when(categoryDao.exists(categoryId)).thenReturn(true);
        when(recipeDao.addRecipe(any(Recipe.class))).thenReturn(true);

        // When
        RecipeService.RecipeResult result = recipeService.addRecipe(name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getRecipe()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("食谱添加成功！");
        verify(categoryDao).exists(categoryId);
        verify(recipeDao).addRecipe(any(Recipe.class));
    }

    @Test
    void testAddRecipeInvalidName() {
        // Given
        String name = ""; // 无效名称
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;

        // When
        RecipeService.RecipeResult result = recipeService.addRecipe(name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("食谱名称不能为空");
        verify(categoryDao, never()).exists(anyInt());
        verify(recipeDao, never()).addRecipe(any(Recipe.class));
    }

    @Test
    void testAddRecipeNameTooShort() {
        // Given
        String name = "a"; // 名称太短
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;

        // When
        RecipeService.RecipeResult result = recipeService.addRecipe(name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("食谱名称长度必须在2-100个字符之间");
        verify(categoryDao, never()).exists(anyInt());
        verify(recipeDao, never()).addRecipe(any(Recipe.class));
    }

    @Test
    void testAddRecipeNameTooLong() {
        // Given
        String name = "这是一个非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常长的食谱名称";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;

        // When
        RecipeService.RecipeResult result = recipeService.addRecipe(name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("食谱名称长度必须在2-100个字符之间");
        verify(categoryDao, never()).exists(anyInt());
        verify(recipeDao, never()).addRecipe(any(Recipe.class));
    }

    @Test
    void testAddRecipeEmptyInstructions() {
        // Given
        String name = "红烧肉";
        String instructions = ""; // 空制作步骤
        int categoryId = 1;
        int userId = 1;

        // When
        RecipeService.RecipeResult result = recipeService.addRecipe(name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("制作步骤不能为空");
        verify(categoryDao, never()).exists(anyInt());
        verify(recipeDao, never()).addRecipe(any(Recipe.class));
    }

    @Test
    void testAddRecipeInstructionsTooShort() {
        // Given
        String name = "红烧肉";
        String instructions = "步骤太短"; // 少于10个字符
        int categoryId = 1;
        int userId = 1;

        // When
        RecipeService.RecipeResult result = recipeService.addRecipe(name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("制作步骤至少需要10个字符");
        verify(categoryDao, never()).exists(anyInt());
        verify(recipeDao, never()).addRecipe(any(Recipe.class));
    }

    @Test
    void testAddRecipeInvalidCategory() {
        // Given
        String name = "红烧肉";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 999; // 不存在的分类
        int userId = 1;

        when(categoryDao.exists(categoryId)).thenReturn(false);

        // When
        RecipeService.RecipeResult result = recipeService.addRecipe(name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("分类不存在，请选择有效的分类");
        verify(categoryDao).exists(categoryId);
        verify(recipeDao, never()).addRecipe(any(Recipe.class));
    }

    @Test
    void testAddRecipeDatabaseFailure() {
        // Given
        String name = "红烧肉";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;

        when(categoryDao.exists(categoryId)).thenReturn(true);
        when(recipeDao.addRecipe(any(Recipe.class))).thenReturn(false);

        // When
        RecipeService.RecipeResult result = recipeService.addRecipe(name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("食谱添加失败，请稍后重试");
        verify(categoryDao).exists(categoryId);
        verify(recipeDao).addRecipe(any(Recipe.class));
    }

    @Test
    void testUpdateRecipeSuccess() {
        // Given
        int recipeId = 1;
        String name = "红烧肉更新版";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮\n4. 收汁";
        int categoryId = 1;
        int userId = 1;

        Recipe existingRecipe = createRecipe(recipeId, "原食谱", "原步骤", categoryId, userId);
        
        when(categoryDao.exists(categoryId)).thenReturn(true);
        when(recipeDao.getRecipeById(recipeId)).thenReturn(existingRecipe);
        when(recipeDao.updateRecipe(any(Recipe.class))).thenReturn(true);

        // When
        RecipeService.RecipeResult result = recipeService.updateRecipe(recipeId, name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getRecipe()).isNotNull();
        assertThat(result.getRecipe().getName()).isEqualTo(name);
        assertThat(result.getMessage()).isEqualTo("食谱更新成功！");
        verify(recipeDao).getRecipeById(recipeId);
        verify(categoryDao).exists(categoryId);
        verify(recipeDao).updateRecipe(any(Recipe.class));
    }

    @Test
    void testUpdateRecipeNotFound() {
        // Given
        int recipeId = 999;
        String name = "红烧肉更新版";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;

        when(recipeDao.getRecipeById(recipeId)).thenReturn(null);

        // When
        RecipeService.RecipeResult result = recipeService.updateRecipe(recipeId, name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("食谱不存在");
        verify(recipeDao).getRecipeById(recipeId);
        verify(categoryDao, never()).exists(anyInt());
        verify(recipeDao, never()).updateRecipe(any(Recipe.class));
    }

    @Test
    void testUpdateRecipeInvalidCategory() {
        // Given
        int recipeId = 1;
        String name = "红烧肉更新版";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 999;
        int userId = 1;

        Recipe existingRecipe = createRecipe(recipeId, "原食谱", "原步骤", 1, userId);
        
        when(recipeDao.getRecipeById(recipeId)).thenReturn(existingRecipe);
        when(categoryDao.exists(categoryId)).thenReturn(false);

        // When
        RecipeService.RecipeResult result = recipeService.updateRecipe(recipeId, name, instructions, categoryId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("分类不存在，请选择有效的分类");
        verify(recipeDao).getRecipeById(recipeId);
        verify(categoryDao).exists(categoryId);
        verify(recipeDao, never()).updateRecipe(any(Recipe.class));
    }

    @Test
    void testDeleteRecipeSuccess() {
        // Given
        int recipeId = 1;
        int userId = 1;

        Recipe existingRecipe = createRecipe(recipeId, "原食谱", "原步骤", 1, userId);
        
        when(recipeDao.getRecipeById(recipeId)).thenReturn(existingRecipe);
        when(recipeDao.deleteRecipe(recipeId)).thenReturn(true);

        // When
        RecipeService.RecipeResult result = recipeService.deleteRecipe(recipeId, userId);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("食谱删除成功！");
        verify(recipeDao).getRecipeById(recipeId);
        verify(recipeDao).deleteRecipe(recipeId);
    }

    @Test
    void testDeleteRecipeNotFound() {
        // Given
        int recipeId = 999;
        int userId = 1;

        when(recipeDao.getRecipeById(recipeId)).thenReturn(null);

        // When
        RecipeService.RecipeResult result = recipeService.deleteRecipe(recipeId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("食谱不存在");
        verify(recipeDao).getRecipeById(recipeId);
        verify(recipeDao, never()).deleteRecipe(recipeId);
    }

    @Test
    void testDeleteRecipeDatabaseFailure() {
        // Given
        int recipeId = 1;
        int userId = 1;

        Recipe existingRecipe = createRecipe(recipeId, "原食谱", "原步骤", 1, userId);
        
        when(recipeDao.getRecipeById(recipeId)).thenReturn(existingRecipe);
        when(recipeDao.deleteRecipe(recipeId)).thenReturn(false);

        // When
        RecipeService.RecipeResult result = recipeService.deleteRecipe(recipeId, userId);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("食谱删除失败，请稍后重试");
        verify(recipeDao).getRecipeById(recipeId);
        verify(recipeDao).deleteRecipe(recipeId);
    }

    @Test
    void testGetRecipeByIdSuccess() {
        // Given
        int recipeId = 1;
        int categoryId = 1;
        
        Recipe recipe = createRecipe(recipeId, "测试食谱", "测试步骤", categoryId, 1);
        Category category = new Category(categoryId, "测试分类");
        List<RecipeIngredient> ingredients = Arrays.asList(
            createRecipeIngredient(1, recipeId, 1, "100")
        );

        when(recipeDao.getRecipeById(recipeId)).thenReturn(recipe);
        when(categoryDao.getCategoryById(categoryId)).thenReturn(category);
        when(recipeIngredientDao.getIngredientsByRecipeId(recipeId)).thenReturn(ingredients);

        // When
        Recipe result = recipeService.getRecipeById(recipeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(recipeId);
        assertThat(result.getCategory()).isEqualTo(category);
        assertThat(result.getIngredients()).isEqualTo(ingredients);
        verify(recipeDao).getRecipeById(recipeId);
        verify(categoryDao).getCategoryById(categoryId);
        verify(recipeIngredientDao).getIngredientsByRecipeId(recipeId);
    }

    @Test
    void testGetRecipeByIdNotFound() {
        // Given
        int recipeId = 999;

        when(recipeDao.getRecipeById(recipeId)).thenReturn(null);

        // When
        Recipe result = recipeService.getRecipeById(recipeId);

        // Then
        assertThat(result).isNull();
        verify(recipeDao).getRecipeById(recipeId);
        verify(categoryDao, never()).getCategoryById(anyInt());
        verify(recipeIngredientDao, never()).getIngredientsByRecipeId(anyInt());
    }

    @Test
    void testGetAllRecipes() {
        // Given
        List<Recipe> recipes = Arrays.asList(
            createRecipe(1, "食谱1", "步骤1", 1, 1),
            createRecipe(2, "食谱2", "步骤2", 1, 1)
        );

        when(recipeDao.getAllRecipes()).thenReturn(recipes);

        // When
        List<Recipe> result = recipeService.getAllRecipes();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        verify(recipeDao).getAllRecipes();
        // loadRecipeDetails方法会被调用
    }

    @Test
    void testGetRecipesByCategory() {
        // Given
        int categoryId = 1;
        List<Recipe> recipes = Arrays.asList(
            createRecipe(1, "分类食谱", "步骤", categoryId, 1)
        );

        when(recipeDao.getRecipesByCategory(categoryId)).thenReturn(recipes);

        // When
        List<Recipe> result = recipeService.getRecipesByCategory(categoryId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getCategoryId()).isEqualTo(categoryId);
        verify(recipeDao).getRecipesByCategory(categoryId);
    }

    @Test
    void testSearchRecipesWithKeyword() {
        // Given
        String keyword = "红烧";
        List<Recipe> recipes = Arrays.asList(
            createRecipe(1, "红烧肉", "制作步骤", 1, 1)
        );

        when(recipeDao.searchRecipes(keyword)).thenReturn(recipes);

        // When
        List<Recipe> result = recipeService.searchRecipes(keyword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(recipeDao).searchRecipes(keyword);
    }

    @Test
    void testSearchRecipesEmptyKeyword() {
        // Given
        String keyword = "";
        List<Recipe> allRecipes = Arrays.asList(
            createRecipe(1, "食谱1", "步骤1", 1, 1),
            createRecipe(2, "食谱2", "步骤2", 1, 1)
        );

        when(recipeDao.getAllRecipes()).thenReturn(allRecipes);

        // When
        List<Recipe> result = recipeService.searchRecipes(keyword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        verify(recipeDao).getAllRecipes();
        verify(recipeDao, never()).searchRecipes(anyString());
    }

    @Test
    void testSearchRecipesNullKeyword() {
        // Given
        String keyword = null;
        List<Recipe> allRecipes = Arrays.asList(
            createRecipe(1, "食谱1", "步骤1", 1, 1)
        );

        when(recipeDao.getAllRecipes()).thenReturn(allRecipes);

        // When
        List<Recipe> result = recipeService.searchRecipes(keyword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(recipeDao).getAllRecipes();
        verify(recipeDao, never()).searchRecipes(anyString());
    }

    @Test
    void testGetRecipesByUser() {
        // Given
        int userId = 1;
        List<Recipe> recipes = Arrays.asList(
            createRecipe(1, "用户食谱", "用户步骤", 1, userId)
        );

        when(recipeDao.getRecipesByUser(userId)).thenReturn(recipes);

        // When
        List<Recipe> result = recipeService.getRecipesByUser(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
        verify(recipeDao).getRecipesByUser(userId);
    }

    @Test
    void testAddRecipeWithIngredientsSuccess() {
        // Given
        String name = "红烧肉";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;
        List<RecipeIngredient> ingredients = Arrays.asList(
            createRecipeIngredient(1, 1, 1, "500")
        );

        when(categoryDao.exists(categoryId)).thenReturn(true);
        when(recipeDao.addRecipe(any(Recipe.class))).thenReturn(true);
        when(recipeIngredientDao.addRecipeIngredients(anyInt(), anyList())).thenReturn(true);

        // When
        RecipeService.RecipeResult result = recipeService.addRecipeWithIngredients(name, instructions, categoryId, userId, ingredients);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getRecipe()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("食谱添加成功！");
        verify(categoryDao).exists(categoryId);
        verify(recipeDao).addRecipe(any(Recipe.class));
        verify(recipeIngredientDao).addRecipeIngredients(anyInt(), anyList());
    }

    @Test
    void testAddRecipeWithIngredientsDatabaseFailure() {
        // Given
        String name = "红烧肉";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;
        List<RecipeIngredient> ingredients = Arrays.asList(
            createRecipeIngredient(1, 1, 1, "500")
        );

        when(categoryDao.exists(categoryId)).thenReturn(true);
        when(recipeDao.addRecipe(any(Recipe.class))).thenReturn(false);

        // When
        RecipeService.RecipeResult result = recipeService.addRecipeWithIngredients(name, instructions, categoryId, userId, ingredients);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("食谱添加失败，请稍后重试");
        verify(categoryDao).exists(categoryId);
        verify(recipeDao).addRecipe(any(Recipe.class));
        verify(recipeIngredientDao, never()).addRecipeIngredients(anyInt(), anyList());
    }

    @Test
    void testAddRecipeWithIngredientsFailureButRecipeCreated() {
        // Given
        String name = "红烧肉";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;
        List<RecipeIngredient> ingredients = Arrays.asList(
            createRecipeIngredient(1, 1, 1, "500")
        );

        when(categoryDao.exists(categoryId)).thenReturn(true);
        when(recipeDao.addRecipe(any(Recipe.class))).thenReturn(true);
        when(recipeIngredientDao.addRecipeIngredients(anyInt(), anyList())).thenReturn(false);

        // When
        RecipeService.RecipeResult result = recipeService.addRecipeWithIngredients(name, instructions, categoryId, userId, ingredients);

        // Then
        assertThat(result.isSuccess()).isTrue(); // 仍然成功，因为食谱已创建
        assertThat(result.getRecipe()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("食谱添加成功！");
        verify(categoryDao).exists(categoryId);
        verify(recipeDao).addRecipe(any(Recipe.class));
        verify(recipeIngredientDao).addRecipeIngredients(anyInt(), anyList());
    }

    @Test
    void testUpdateRecipeWithIngredientsSuccess() {
        // Given
        int recipeId = 1;
        String name = "红烧肉更新版";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮\n4. 收汁";
        int categoryId = 1;
        int userId = 1;
        List<RecipeIngredient> ingredients = Arrays.asList(
            createRecipeIngredient(1, recipeId, 1, "600")
        );

        Recipe existingRecipe = createRecipe(recipeId, "原食谱", "原步骤", categoryId, userId);
        
        when(categoryDao.exists(categoryId)).thenReturn(true);
        when(recipeDao.getRecipeById(recipeId)).thenReturn(existingRecipe);
        when(recipeDao.updateRecipe(any(Recipe.class))).thenReturn(true);
        when(recipeIngredientDao.updateRecipeIngredients(recipeId, ingredients)).thenReturn(true);

        // When
        RecipeService.RecipeResult result = recipeService.updateRecipeWithIngredients(recipeId, name, instructions, categoryId, userId, ingredients);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getRecipe()).isNotNull();
        assertThat(result.getRecipe().getName()).isEqualTo(name);
        assertThat(result.getMessage()).isEqualTo("食谱更新成功！");
        verify(recipeDao).getRecipeById(recipeId);
        verify(categoryDao).exists(categoryId);
        verify(recipeDao).updateRecipe(any(Recipe.class));
        verify(recipeIngredientDao).updateRecipeIngredients(recipeId, ingredients);
    }

    @Test
    void testUpdateRecipeWithIngredientsRecipeNotFound() {
        // Given
        int recipeId = 999;
        String name = "红烧肉更新版";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;
        List<RecipeIngredient> ingredients = Arrays.asList(
            createRecipeIngredient(1, recipeId, 1, "500")
        );

        when(recipeDao.getRecipeById(recipeId)).thenReturn(null);

        // When
        RecipeService.RecipeResult result = recipeService.updateRecipeWithIngredients(recipeId, name, instructions, categoryId, userId, ingredients);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getRecipe()).isNull();
        assertThat(result.getMessage()).isEqualTo("食谱不存在");
        verify(recipeDao).getRecipeById(recipeId);
        verify(categoryDao, never()).exists(anyInt());
        verify(recipeDao, never()).updateRecipe(any(Recipe.class));
        verify(recipeIngredientDao, never()).updateRecipeIngredients(anyInt(), anyList());
    }

    @Test
    void testUpdateRecipeWithIngredientsNullIngredients() {
        // Given
        int recipeId = 1;
        String name = "红烧肉更新版";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;
        int userId = 1;

        Recipe existingRecipe = createRecipe(recipeId, "原食谱", "原步骤", categoryId, userId);
        
        when(categoryDao.exists(categoryId)).thenReturn(true);
        when(recipeDao.getRecipeById(recipeId)).thenReturn(existingRecipe);
        when(recipeDao.updateRecipe(any(Recipe.class))).thenReturn(true);

        // When
        RecipeService.RecipeResult result = recipeService.updateRecipeWithIngredients(recipeId, name, instructions, categoryId, userId, null);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getRecipe()).isNotNull();
        assertThat(result.getMessage()).isEqualTo("食谱更新成功！");
        verify(recipeDao).getRecipeById(recipeId);
        verify(categoryDao).exists(categoryId);
        verify(recipeDao).updateRecipe(any(Recipe.class));
        verify(recipeIngredientDao, never()).updateRecipeIngredients(anyInt(), anyList());
    }

    @Test
    void testValidateRecipeInputValid() {
        // Given
        String name = "红烧肉";
        String instructions = "1. 将肉切块\n2. 炒糖色\n3. 加水炖煮";
        int categoryId = 1;

        // When & Then - 通过公共方法测试私有验证逻辑
        RecipeService.RecipeResult result = recipeService.addRecipe(name, instructions, categoryId, 1);

        // 验证通过的情况应该到达数据库调用阶段
        assertThat(result.isSuccess() || result.getMessage().contains("分类不存在") || result.getMessage().contains("添加失败")).isTrue();
    }

    @Test
    void testValidateRecipeInputInvalidName() {
        // When & Then - 通过公共方法测试私有验证逻辑
        RecipeService.RecipeResult result = recipeService.addRecipe("", "有效的制作步骤", 1, 1);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("食谱名称不能为空");
    }

    @Test
    void testValidateRecipeInputInvalidInstructions() {
        // When & Then - 通过公共方法测试私有验证逻辑
        RecipeService.RecipeResult result = recipeService.addRecipe("有效名称", "", 1, 1);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("制作步骤不能为空");
    }

    @Test
    void testValidateRecipeInputInvalidCategory() {
        // When & Then - 通过公共方法测试私有验证逻辑
        RecipeService.RecipeResult result = recipeService.addRecipe("有效名称", "有效的制作步骤", 0, 1);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("请选择有效的分类");
    }
}
