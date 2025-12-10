package com.chang1o.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Recipe模型测试")
class RecipeTest {

    private Recipe recipe;
    private Category category;

    @BeforeEach
    void setUp() {
        recipe = new Recipe();
        category = new Category(1, "Italian");
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        Recipe defaultRecipe = new Recipe();
        assertThat(defaultRecipe).isNotNull();
        assertThat(defaultRecipe.getId()).isEqualTo(0);
        assertThat(defaultRecipe.getName()).isNull();
        assertThat(defaultRecipe.getInstructions()).isNull();
        assertThat(defaultRecipe.getCategoryId()).isEqualTo(0);
        assertThat(defaultRecipe.getUserId()).isEqualTo(0);
        assertThat(defaultRecipe.getCategory()).isNull();
        assertThat(defaultRecipe.getIngredients()).isNull();
    }

    @Test
    @DisplayName("测试带参数的构造函数")
    void testConstructorWithParameters() {
        String name = "Spaghetti Carbonara";
        String instructions = "1. Boil pasta 2. Mix eggs and cheese...";
        int categoryId = 1;
        int userId = 123;
        
        Recipe recipeWithParams = new Recipe(name, instructions, categoryId, userId);
        
        assertThat(recipeWithParams).isNotNull();
        assertThat(recipeWithParams.getName()).isEqualTo(name);
        assertThat(recipeWithParams.getInstructions()).isEqualTo(instructions);
        assertThat(recipeWithParams.getCategoryId()).isEqualTo(categoryId);
        assertThat(recipeWithParams.getUserId()).isEqualTo(userId);
        assertThat(recipeWithParams.getCategory()).isNull();
        assertThat(recipeWithParams.getIngredients()).isNull();
    }

    @Test
    @DisplayName("测试完整构造函数")
    void testConstructorWithAllParameters() {
        int id = 1;
        String name = "Spaghetti Carbonara";
        String instructions = "1. Boil pasta 2. Mix eggs and cheese...";
        int categoryId = 1;
        int userId = 123;
        
        Recipe recipeWithAllParams = new Recipe(id, name, instructions, categoryId, category, userId);
        
        assertThat(recipeWithAllParams).isNotNull();
        assertThat(recipeWithAllParams.getId()).isEqualTo(id);
        assertThat(recipeWithAllParams.getName()).isEqualTo(name);
        assertThat(recipeWithAllParams.getInstructions()).isEqualTo(instructions);
        assertThat(recipeWithAllParams.getCategoryId()).isEqualTo(categoryId);
        assertThat(recipeWithAllParams.getCategory()).isEqualTo(category);
        assertThat(recipeWithAllParams.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("测试set和get方法")
    void testSetAndGetMethods() {
        int id = 10;
        String name = "Caesar Salad";
        String instructions = "1. Wash lettuce 2. Add dressing...";
        int categoryId = 2;
        int userId = 456;
        
        recipe.setId(id);
        recipe.setName(name);
        recipe.setInstructions(instructions);
        recipe.setCategoryId(categoryId);
        recipe.setUserId(userId);
        recipe.setCategory(category);
        
        assertThat(recipe.getId()).isEqualTo(id);
        assertThat(recipe.getName()).isEqualTo(name);
        assertThat(recipe.getInstructions()).isEqualTo(instructions);
        assertThat(recipe.getUserId()).isEqualTo(userId);
        assertThat(recipe.getCategory()).isEqualTo(category);
        assertThat(recipe.getCategoryId()).isEqualTo(category.getId());
    }

    @Test
    @DisplayName("测试setCategory方法会自动更新categoryId")
    void testSetCategoryUpdatesCategoryId() {
        recipe.setCategoryId(0);
        assertThat(recipe.getCategoryId()).isEqualTo(0);
        
        recipe.setCategory(category);
        assertThat(recipe.getCategory()).isEqualTo(category);
        assertThat(recipe.getCategoryId()).isEqualTo(category.getId());
    }

    @Test
    @DisplayName("测试setCategory为null")
    void testSetCategoryNull() {
        recipe.setCategory(category);
        assertThat(recipe.getCategory()).isNotNull();
        
        recipe.setCategory(null);
        assertThat(recipe.getCategory()).isNull();
        assertThat(recipe.getCategoryId()).isEqualTo(category.getId());
    }

    @Test
    @DisplayName("测试ingredients列表操作")
    void testIngredientsListOperations() {
        assertThat(recipe.getIngredients()).isNull();
        
        // 设置ingredients列表
        var ingredients = new java.util.ArrayList<RecipeIngredient>();
        recipe.setIngredients(ingredients);
        assertThat(recipe.getIngredients()).isEqualTo(ingredients);
        
        RecipeIngredient ingredient = new RecipeIngredient();
        recipe.addIngredient(ingredient);
        assertThat(recipe.getIngredients()).hasSize(1);
        assertThat(recipe.getIngredients()).contains(ingredient);
    }

    @Test
    @DisplayName("测试addIngredient方法创建新列表")
    void testAddIngredientCreatesNewList() {
        assertThat(recipe.getIngredients()).isNull();
        
        RecipeIngredient ingredient = new RecipeIngredient();
        recipe.addIngredient(ingredient);
        
        assertThat(recipe.getIngredients()).isNotNull();
        assertThat(recipe.getIngredients()).hasSize(1);
        assertThat(recipe.getIngredients()).contains(ingredient);
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        int id = 1;
        String name = "Pasta";
        String instructions = "Cook pasta";
        int categoryId = 1;
        int userId = 100;
        
        recipe.setId(id);
        recipe.setName(name);
        recipe.setInstructions(instructions);
        recipe.setCategoryId(categoryId);
        recipe.setUserId(userId);
        recipe.setCategory(category);
        
        String result = recipe.toString();
        
        assertThat(result).contains("Recipe{id" + id);
        assertThat(result).contains("name='" + name + "'");
        assertThat(result).contains("instructions=" + instructions);
        assertThat(result).contains("categoryId=" + categoryId);
        assertThat(result).contains("userId=" + userId);
        assertThat(result).contains("category=" + category.getName());
    }

    @Test
    @DisplayName("测试toString方法不包含category的情况")
    void testToStringWithoutCategory() {
        recipe.setId(1);
        recipe.setName("Simple Recipe");
        recipe.setInstructions("Simple instructions");
        
        String result = recipe.toString();
        
        assertThat(result).contains("Recipe{id1");
        assertThat(result).contains("name='Simple Recipe'");
        assertThat(result).contains("category=null");
    }

    @Test
    @DisplayName("测试边界值")
    void testBoundaryValues() {
        recipe.setId(Integer.MIN_VALUE);
        assertThat(recipe.getId()).isEqualTo(Integer.MIN_VALUE);
        
        recipe.setId(Integer.MAX_VALUE);
        assertThat(recipe.getId()).isEqualTo(Integer.MAX_VALUE);
        
        recipe.setId(0);
        assertThat(recipe.getId()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试字符串字段的边界值")
    void testStringFieldBoundaryValues() {
        recipe.setName("");
        assertThat(recipe.getName()).isEmpty();
        
        recipe.setInstructions("");
        assertThat(recipe.getInstructions()).isEmpty();
        
        String longName = "a".repeat(1000);
        String longInstructions = "b".repeat(5000);
        
        recipe.setName(longName);
        recipe.setInstructions(longInstructions);
        
        assertThat(recipe.getName()).isEqualTo(longName);
        assertThat(recipe.getInstructions()).isEqualTo(longInstructions);
    }

    @Test
    @DisplayName("测试特殊字符")
    void testSpecialCharacters() {
        String specialName = "Spaghetti à la Carbonara! 🎉";
        String specialInstructions = "1. Add salt & pepper 2. Mix with 🧀 and 🍳";
        
        recipe.setName(specialName);
        recipe.setInstructions(specialInstructions);
        
        assertThat(recipe.getName()).isEqualTo(specialName);
        assertThat(recipe.getInstructions()).isEqualTo(specialInstructions);
    }
}
