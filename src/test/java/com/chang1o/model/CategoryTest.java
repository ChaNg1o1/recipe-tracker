package com.chang1o.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Category模型测试")
class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    @DisplayName("测试默认构造函数")
    void testDefaultConstructor() {
        Category defaultCategory = new Category();
        assertThat(defaultCategory).isNotNull();
        assertThat(defaultCategory.getId()).isEqualTo(0);
        assertThat(defaultCategory.getName()).isNull();
    }

    @Test
    @DisplayName("测试带名称的构造函数")
    void testConstructorWithName() {
        String name = "Italian";
        Category categoryWithName = new Category(name);
        
        assertThat(categoryWithName).isNotNull();
        assertThat(categoryWithName.getName()).isEqualTo(name);
        assertThat(categoryWithName.getId()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试完整构造函数")
    void testConstructorWithAllParameters() {
        int id = 1;
        String name = "Chinese";
        
        Category categoryWithAll = new Category(id, name);
        
        assertThat(categoryWithAll).isNotNull();
        assertThat(categoryWithAll.getId()).isEqualTo(id);
        assertThat(categoryWithAll.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("测试set和get方法")
    void testSetAndGetMethods() {
        int expectedId = 5;
        String expectedName = "Mexican";
        
        category.setId(expectedId);
        category.setName(expectedName);
        
        assertThat(category.getId()).isEqualTo(expectedId);
        assertThat(category.getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        int id = 10;
        String name = "Dessert";
        
        category.setId(id);
        category.setName(name);
        
        String result = category.toString();
        
        assertThat(result).contains("Category{");
        assertThat(result).contains("id" + id);
        assertThat(result).contains("name" + name);
        assertThat(result).isEqualTo("Category{id" + id + ",name" + name + "}");
    }

    @Test
    @DisplayName("测试toString方法包含null值")
    void testToStringWithNullValues() {
        String result = category.toString();
        
        assertThat(result).contains("Category{id0,namenull}");
    }

    @Test
    @DisplayName("测试边界值")
    void testBoundaryValues() {
        category.setId(Integer.MIN_VALUE);
        assertThat(category.getId()).isEqualTo(Integer.MIN_VALUE);
        
        category.setId(Integer.MAX_VALUE);
        assertThat(category.getId()).isEqualTo(Integer.MAX_VALUE);
        
        category.setId(0);
        assertThat(category.getId()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试名称字段的边界值")
    void testNameFieldBoundaryValues() {
        category.setName("");
        assertThat(category.getName()).isEmpty();
        
        String longName = "a".repeat(500);
        category.setName(longName);
        assertThat(category.getName()).isEqualTo(longName);
        
        category.setName(null);
        assertThat(category.getName()).isNull();
    }

    @Test
    @DisplayName("测试特殊字符和Unicode")
    void testSpecialCharactersAndUnicode() {
        category.setName("中式菜系");
        assertThat(category.getName()).isEqualTo("中式菜系");
        
        String specialName = "Fusion Cuisine & More! 🎨";
        category.setName(specialName);
        assertThat(category.getName()).isEqualTo(specialName);
        
        String emojiName = "🍕 Pizza 🍝 Pasta";
        category.setName(emojiName);
        assertThat(category.getName()).isEqualTo(emojiName);
    }

    @Test
    @DisplayName("测试常见的食谱分类")
    void testCommonRecipeCategories() {
        String[] commonCategories = {
            "Breakfast", "Lunch", "Dinner", "Appetizer", "Dessert",
            "Beverage", "Snack", "Soup", "Salad", "Main Course",
            "Side Dish", "Baking", "Grilling", "Vegetarian", "Vegan",
            "Gluten-Free", "Low-Carb", "Keto", "Mediterranean", "Asian"
        };
        
        for (String categoryName : commonCategories) {
            category.setName(categoryName);
            assertThat(category.getName()).isEqualTo(categoryName);
            
            String toStringResult = category.toString();
            assertThat(toStringResult).contains("name" + categoryName);
        }
    }

    @Test
    @DisplayName("测试ID和名称的关联性")
    void testIdAndNameAssociation() {
        category.setName("First");
        category.setId(100);
        
        assertThat(category.getId()).isEqualTo(100);
        assertThat(category.getName()).isEqualTo("First");
        
        category.setId(200);
        category.setName("Second");
        
        assertThat(category.getId()).isEqualTo(200);
        assertThat(category.getName()).isEqualTo("Second");
    }

    @Test
    @DisplayName("测试Equals和HashCode")
    void testEqualsAndHashCode() {
        Category category1 = new Category(1, "Italian");
        Category category2 = new Category(1, "Italian");
        Category category3 = new Category(2, "Mexican");
        
        assertThat(category1).isEqualTo(category2);
        assertThat(category1).isNotEqualTo(category3);
        assertThat(category1).isNotEqualTo(null);
        assertThat(category1).isNotEqualTo("not a category");
        
        assertThat(category1.hashCode()).isEqualTo(category2.hashCode());
    }

    @Test
    @DisplayName("测试构造函数的所有组合")
    void testAllConstructorCombinations() {
        Category defaultCat = new Category();
        assertThat(defaultCat.getId()).isEqualTo(0);
        assertThat(defaultCat.getName()).isNull();
        
        String name = "Test Category";
        Category nameOnlyCat = new Category(name);
        assertThat(nameOnlyCat.getId()).isEqualTo(0);
        assertThat(nameOnlyCat.getName()).isEqualTo(name);
        
        int id = 42;
        Category fullCat = new Category(id, name);
        assertThat(fullCat.getId()).isEqualTo(id);
        assertThat(fullCat.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("测试属性修改后的状态一致性")
    void testStateConsistencyAfterModification() {
        category.setId(1);
        category.setName("Initial");
        
        category.setId(2);
        assertThat(category.getId()).isEqualTo(2);
        assertThat(category.getName()).isEqualTo("Initial");
        
        category.setName("Modified");
        assertThat(category.getId()).isEqualTo(2);
        assertThat(category.getName()).isEqualTo("Modified");
        
        category.setId(3);
        assertThat(category.getId()).isEqualTo(3);
        assertThat(category.getName()).isEqualTo("Modified");
    }

    @Test
    @DisplayName("测试toString格式的完整性")
    void testToStringFormatCompleteness() {
        // 测试toString确实包含了所有字段
        category.setId(42);
        category.setName("TestCategory");
        
        String result = category.toString();
        
        assertThat(result).startsWith("Category{");
        assertThat(result).endsWith("}");
        assertThat(result).contains("id42");
        assertThat(result).contains("nameTestCategory");
        
        Category cat1 = new Category(0, null);
        String result1 = cat1.toString();
        assertThat(result1).isEqualTo("Category{id0,namenull}");
        
        Category cat2 = new Category(100, "Special");
        String result2 = cat2.toString();
        assertThat(result2).isEqualTo("Category{id100,nameSpecial}");
    }

    @Test
    @DisplayName("测试多语言分类名称")
    void testMultilingualCategoryNames() {
        // 测试不同语言的分类名称
        String[] multilingualCategories = {
            "早餐", "午餐", "晚餐", "开胃菜", "甜点",
            "Petit déjeuner", "Déjeuner", "Dîner", "Entrée", "Dessert",
            " Frühstück", "Mittagessen", "Abendessen", "Vorspeise", "Nachtisch",
            " завтрак", "обед", "ужин", "закуска", "десерт"
        };
        
        for (String categoryName : multilingualCategories) {
            category.setName(categoryName);
            assertThat(category.getName()).isEqualTo(categoryName);
            
            String toStringResult = category.toString();
            assertThat(toStringResult).contains("name" + categoryName);
        }
    }

    @Test
    @DisplayName("测试分类名称的字符限制")
    void testCategoryNameCharacterLimits() {
        category.setName("A");
        assertThat(category.getName()).isEqualTo("A");
        
        String mediumName = "Mediterranean Fusion Cuisine";
        category.setName(mediumName);
        assertThat(category.getName()).isEqualTo(mediumName);
        
        String veryLongName = "This is an extremely long category name that might be used in some specialized cooking systems".repeat(10);
        category.setName(veryLongName);
        assertThat(category.getName()).isEqualTo(veryLongName);
    }
}
