package com.chang1o.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("RecipeIngredient模型测试")
class RecipeIngredientTest {

    private RecipeIngredient recipeIngredient;
    private Ingredient ingredient;

    @BeforeEach
    void setUp() {
        recipeIngredient = new RecipeIngredient();
        ingredient = new Ingredient(1, "Tomato");
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        RecipeIngredient defaultRecipeIngredient = new RecipeIngredient();
        assertThat(defaultRecipeIngredient).isNotNull();
        assertThat(defaultRecipeIngredient.getRecipeId()).isEqualTo(0);
        assertThat(defaultRecipeIngredient.getIngredientId()).isEqualTo(0);
        assertThat(defaultRecipeIngredient.getQuantity()).isNull();
        assertThat(defaultRecipeIngredient.getIngredient()).isNull();
    }

    @Test
    @DisplayName("测试带参数的构造函数")
    void testConstructorWithParameters() {
        int recipeId = 123;
        int ingredientId = 456;
        String quantity = "2个";

        RecipeIngredient recipeIngredientWithParams = new RecipeIngredient(recipeId, ingredientId, quantity);

        assertThat(recipeIngredientWithParams).isNotNull();
        assertThat(recipeIngredientWithParams.getRecipeId()).isEqualTo(recipeId);
        assertThat(recipeIngredientWithParams.getIngredientId()).isEqualTo(ingredientId);
        assertThat(recipeIngredientWithParams.getQuantity()).isEqualTo(quantity);
        assertThat(recipeIngredientWithParams.getIngredient()).isNull();
    }

    @Test
    @DisplayName("测试set和get方法")
    void testSetAndGetMethods() {
        int recipeId = 10;
        int ingredientId = 20;
        String quantity = "500g";

        recipeIngredient.setRecipeId(recipeId);
        recipeIngredient.setIngredientId(ingredientId);
        recipeIngredient.setQuantity(quantity);
        recipeIngredient.setIngredient(ingredient);

        assertThat(recipeIngredient.getRecipeId()).isEqualTo(recipeId);
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(ingredientId);
        assertThat(recipeIngredient.getQuantity()).isEqualTo(quantity);
        assertThat(recipeIngredient.getIngredient()).isEqualTo(ingredient);
    }

    @Test
    @DisplayName("测试setIngredient方法会自动更新ingredientId")
    void testSetIngredientUpdatesIngredientId() {
        recipeIngredient.setIngredientId(0);
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(0);

        recipeIngredient.setIngredient(ingredient);
        assertThat(recipeIngredient.getIngredient()).isEqualTo(ingredient);
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(ingredient.getId());
    }

    @Test
    @DisplayName("测试setIngredient为null")
    void testSetIngredientNull() {
        recipeIngredient.setIngredient(ingredient);
        assertThat(recipeIngredient.getIngredient()).isNotNull();

        recipeIngredient.setIngredient(null);
        assertThat(recipeIngredient.getIngredient()).isNull();
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(ingredient.getId()); // 不应该改变
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        recipeIngredient.setRecipeId(1);
        recipeIngredient.setIngredientId(2);
        recipeIngredient.setQuantity("100g");
        recipeIngredient.setIngredient(ingredient);

        String result = recipeIngredient.toString();

        assertThat(result).contains("RecipeIngredient{");
        assertThat(result).contains("recipeId=1");
        assertThat(result).contains("ingredientId=2");
        assertThat(result).contains("quantity='100g'");
        assertThat(result).contains("ingredient=Tomato");
    }

    @Test
    @DisplayName("测试toString方法包含null值")
    void testToStringWithNullValues() {
        String result = recipeIngredient.toString();

        assertThat(result).contains("RecipeIngredient{");
        assertThat(result).contains("recipeId=0");
        assertThat(result).contains("ingredientId=0");
        assertThat(result).contains("quantity='null'");
        assertThat(result).contains("ingredient=null");
    }

    @Test
    @DisplayName("测试toString方法当ingredient为null")
    void testToStringWithNullIngredient() {
        recipeIngredient.setRecipeId(1);
        recipeIngredient.setIngredientId(2);
        recipeIngredient.setQuantity("100g");
        recipeIngredient.setIngredient(null);

        String result = recipeIngredient.toString();

        assertThat(result).contains("RecipeIngredient{");
        assertThat(result).contains("recipeId=1");
        assertThat(result).contains("ingredientId=2");
        assertThat(result).contains("quantity='100g'");
        assertThat(result).contains("ingredient=null");
    }

    @Test
    @DisplayName("测试边界值")
    void testBoundaryValues() {
        recipeIngredient.setRecipeId(Integer.MIN_VALUE);
        assertThat(recipeIngredient.getRecipeId()).isEqualTo(Integer.MIN_VALUE);

        recipeIngredient.setRecipeId(Integer.MAX_VALUE);
        assertThat(recipeIngredient.getRecipeId()).isEqualTo(Integer.MAX_VALUE);

        recipeIngredient.setIngredientId(Integer.MIN_VALUE);
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(Integer.MIN_VALUE);

        recipeIngredient.setIngredientId(Integer.MAX_VALUE);
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    @DisplayName("测试数量字段的边界值")
    void testQuantityFieldBoundaryValues() {
        recipeIngredient.setQuantity("");
        assertThat(recipeIngredient.getQuantity()).isEmpty();

        String longQuantity = "a".repeat(1000);
        recipeIngredient.setQuantity(longQuantity);
        assertThat(recipeIngredient.getQuantity()).isEqualTo(longQuantity);

        recipeIngredient.setQuantity(null);
        assertThat(recipeIngredient.getQuantity()).isNull();
    }

    @Test
    @DisplayName("测试数量字段的各种格式")
    void testQuantityFieldFormats() {
        String[] quantities = {
            "1kg", "500g", "2L", "3个", "半杯", "适量",
            "1/2杯", "2-3个", ">100g", "<500ml", "一把", "少许"
        };

        for (String quantity : quantities) {
            recipeIngredient.setQuantity(quantity);
            assertThat(recipeIngredient.getQuantity()).isEqualTo(quantity);

            String toStringResult = recipeIngredient.toString();
            assertThat(toStringResult).contains("quantity='" + quantity + "'");
        }
    }

    @Test
    @DisplayName("测试关联的Ingredient对象")
    void testAssociatedIngredient() {
        recipeIngredient.setIngredient(ingredient);
        assertThat(recipeIngredient.getIngredient()).isEqualTo(ingredient);
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(ingredient.getId());

        Ingredient anotherIngredient = new Ingredient(2, "Onion");
        recipeIngredient.setIngredient(anotherIngredient);
        assertThat(recipeIngredient.getIngredient()).isEqualTo(anotherIngredient);
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(anotherIngredient.getId());
    }

    @Test
    @DisplayName("测试完整的RecipeIngredient状态")
    void testCompleteRecipeIngredientState() {
        RecipeIngredient completeRecipeIngredient = new RecipeIngredient(1, 2, "200g");
        completeRecipeIngredient.setIngredient(ingredient);

        assertThat(completeRecipeIngredient.getRecipeId()).isEqualTo(1);
        assertThat(completeRecipeIngredient.getIngredientId()).isEqualTo(2);
        assertThat(completeRecipeIngredient.getQuantity()).isEqualTo("200g");
        assertThat(completeRecipeIngredient.getIngredient()).isEqualTo(ingredient);
        assertThat(completeRecipeIngredient.getIngredient().getName()).isEqualTo("Tomato");
    }

    @Test
    @DisplayName("测试构造函数null参数处理")
    void testConstructorNullHandling() {
        RecipeIngredient recipeIngredient = new RecipeIngredient(1, 2, null);
        assertThat(recipeIngredient.getQuantity()).isNull();
        assertThat(recipeIngredient.getRecipeId()).isEqualTo(1);
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(2);
    }

    @Test
    @DisplayName("测试ID的极值处理")
    void testIdBoundaryValues() {
        recipeIngredient.setRecipeId(Integer.MIN_VALUE);
        assertThat(recipeIngredient.getRecipeId()).isEqualTo(Integer.MIN_VALUE);

        recipeIngredient.setRecipeId(Integer.MAX_VALUE);
        assertThat(recipeIngredient.getRecipeId()).isEqualTo(Integer.MAX_VALUE);

        recipeIngredient.setIngredientId(Integer.MIN_VALUE);
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(Integer.MIN_VALUE);

        recipeIngredient.setIngredientId(Integer.MAX_VALUE);
        assertThat(recipeIngredient.getIngredientId()).isEqualTo(Integer.MAX_VALUE);
    }
}