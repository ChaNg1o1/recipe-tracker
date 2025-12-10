package com.chang1o.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Ingredient模型测试")
class IngredientTest {

    private Ingredient ingredient;

    @BeforeEach
    void setUp() {
        ingredient = new Ingredient();
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        Ingredient defaultIngredient = new Ingredient();
        assertThat(defaultIngredient).isNotNull();
        assertThat(defaultIngredient.getId()).isEqualTo(0);
        assertThat(defaultIngredient.getName()).isNull();
    }

    @Test
    @DisplayName("测试带名称的构造函数")
    void testConstructorWithName() {
        String name = "Tomato";
        Ingredient ingredientWithName = new Ingredient(name);
        
        assertThat(ingredientWithName).isNotNull();
        assertThat(ingredientWithName.getName()).isEqualTo(name);
        assertThat(ingredientWithName.getId()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试完整构造函数")
    void testConstructorWithAllParameters() {
        int id = 1;
        String name = "Mozzarella Cheese";
        
        Ingredient ingredientWithAll = new Ingredient(id, name);
        
        assertThat(ingredientWithAll).isNotNull();
        assertThat(ingredientWithAll.getId()).isEqualTo(id);
        assertThat(ingredientWithAll.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("测试set和get方法")
    void testSetAndGetMethods() {
        int expectedId = 5;
        String expectedName = "Basil";
        
        ingredient.setId(expectedId);
        ingredient.setName(expectedName);
        
        assertThat(ingredient.getId()).isEqualTo(expectedId);
        assertThat(ingredient.getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        int id = 10;
        String name = "Olive Oil";
        
        ingredient.setId(id);
        ingredient.setName(name);
        
        String result = ingredient.toString();
        
        assertThat(result).contains("Ingredient{");
        assertThat(result).contains("id=" + id);
        assertThat(result).contains("name=" + name);
        assertThat(result).isEqualTo("Ingredient{id=" + id + ",name=" + name + "}");
    }

    @Test
    @DisplayName("测试toString方法包含null值")
    void testToStringWithNullValues() {
        String result = ingredient.toString();
        
        assertThat(result).contains("Ingredient{id=0,name=null}");
    }

    @Test
    @DisplayName("测试边界值")
    void testBoundaryValues() {
        ingredient.setId(Integer.MIN_VALUE);
        assertThat(ingredient.getId()).isEqualTo(Integer.MIN_VALUE);
        
        ingredient.setId(Integer.MAX_VALUE);
        assertThat(ingredient.getId()).isEqualTo(Integer.MAX_VALUE);
        
        ingredient.setId(0);
        assertThat(ingredient.getId()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试名称字段的边界值")
    void testNameFieldBoundaryValues() {
        ingredient.setName("");
        assertThat(ingredient.getName()).isEmpty();
        
        String longName = "a".repeat(500);
        ingredient.setName(longName);
        assertThat(ingredient.getName()).isEqualTo(longName);
        
        ingredient.setName(null);
        assertThat(ingredient.getName()).isNull();
    }

    @Test
    @DisplayName("测试特殊字符和Unicode")
    void testSpecialCharactersAndUnicode() {
        ingredient.setName("番茄");
        assertThat(ingredient.getName()).isEqualTo("番茄");
        
        String specialName = "Salt & Pepper 🧂";
        ingredient.setName(specialName);
        assertThat(ingredient.getName()).isEqualTo(specialName);
        
        String emojiName = "🍝 Pasta 🍅";
        ingredient.setName(emojiName);
        assertThat(ingredient.getName()).isEqualTo(emojiName);
    }

    @Test
    @DisplayName("测试常见的食材名称")
    void testCommonIngredientNames() {
        String[] commonIngredients = {
            "Salt", "Sugar", "Flour", "Eggs", "Milk", "Butter",
            "Garlic", "Onion", "Tomato", "Potato", "Carrot",
            "Chicken", "Beef", "Fish", "Rice", "Pasta"
        };
        
        for (String ingredientName : commonIngredients) {
            ingredient.setName(ingredientName);
            assertThat(ingredient.getName()).isEqualTo(ingredientName);
            
            String toStringResult = ingredient.toString();
            assertThat(toStringResult).contains("name=" + ingredientName);
        }
    }

    @Test
    @DisplayName("测试ID和名称的关联性")
    void testIdAndNameAssociation() {
        ingredient.setName("First");
        ingredient.setId(100);
        
        assertThat(ingredient.getId()).isEqualTo(100);
        assertThat(ingredient.getName()).isEqualTo("First");
        
        ingredient.setId(200);
        ingredient.setName("Second");
        
        assertThat(ingredient.getId()).isEqualTo(200);
        assertThat(ingredient.getName()).isEqualTo("Second");
    }

    @Test
    @DisplayName("测试Equals和HashCode")
    void testEqualsAndHashCode() {
        Ingredient ingredient1 = new Ingredient(1, "Salt");
        Ingredient ingredient2 = new Ingredient(1, "Salt");
        Ingredient ingredient3 = new Ingredient(2, "Pepper");
        
        assertThat(ingredient1).isEqualTo(ingredient2);
        assertThat(ingredient1).isNotEqualTo(ingredient3);
        assertThat(ingredient1).isNotEqualTo(null);
        assertThat(ingredient1).isNotEqualTo("not an ingredient");
        
        assertThat(ingredient1.hashCode()).isEqualTo(ingredient2.hashCode());
    }

    @Test
    @DisplayName("测试构造函数的所有组合")
    void testAllConstructorCombinations() {
        Ingredient defaultIng = new Ingredient();
        assertThat(defaultIng.getId()).isEqualTo(0);
        assertThat(defaultIng.getName()).isNull();
        
        String name = "Test Ingredient";
        Ingredient nameOnlyIng = new Ingredient(name);
        assertThat(nameOnlyIng.getId()).isEqualTo(0);
        assertThat(nameOnlyIng.getName()).isEqualTo(name);
        
        int id = 42;
        Ingredient fullIng = new Ingredient(id, name);
        assertThat(fullIng.getId()).isEqualTo(id);
        assertThat(fullIng.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("测试属性修改后的状态一致性")
    void testStateConsistencyAfterModification() {
        ingredient.setId(1);
        ingredient.setName("Initial");
        
        ingredient.setId(2);
        assertThat(ingredient.getId()).isEqualTo(2);
        assertThat(ingredient.getName()).isEqualTo("Initial");
        
        ingredient.setName("Modified");
        assertThat(ingredient.getId()).isEqualTo(2);
        assertThat(ingredient.getName()).isEqualTo("Modified");
        
        ingredient.setId(3);
        assertThat(ingredient.getId()).isEqualTo(3);
        assertThat(ingredient.getName()).isEqualTo("Modified");
    }
}
