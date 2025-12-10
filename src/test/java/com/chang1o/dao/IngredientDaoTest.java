package com.chang1o.dao;

import com.chang1o.model.Ingredient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * IngredientDao单元测试
 * 使用真实的数据库连接进行测试，确保数据库状态正确隔离
 */
class IngredientDaoTest {

    private IngredientDao ingredientDao;

    @BeforeEach
    void setUp() {
        // 设置测试数据库配置
        System.setProperty("DB_CONFIG", "test-database.properties");
        // 重置DBUtil实例以强制重新加载测试配置
        com.chang1o.util.DBUtil.resetInstance();
        ingredientDao = new IngredientDao();
        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        try (var conn = com.chang1o.util.DBUtil.getInstance().getConnection();
             var stmt = conn.createStatement()) {
            // H2数据库清空方式
            stmt.execute("SET REFERENTIAL_INTEGRITY FALSE");
            // 清理所有相关的表以确保完全隔离（按照依赖关系倒序）
            stmt.execute("TRUNCATE TABLE recipe_ingredients");
            stmt.execute("TRUNCATE TABLE pantry");
            stmt.execute("TRUNCATE TABLE daily_check_in");
            stmt.execute("TRUNCATE TABLE user_health_data");
            stmt.execute("TRUNCATE TABLE recipes");
            stmt.execute("TRUNCATE TABLE ingredients");
            stmt.execute("TRUNCATE TABLE categories");
            stmt.execute("TRUNCATE TABLE users");
            stmt.execute("SET REFERENTIAL_INTEGRITY TRUE");
        } catch (Exception e) {
            try (var conn = com.chang1o.util.DBUtil.getInstance().getConnection();
                 var stmt = conn.createStatement()) {
                // 如果表结构清空失败，使用DROP ALL OBJECTS作为备选方案
                stmt.execute("DROP ALL OBJECTS");
            } catch (Exception ex) {
                System.err.println("清理测试数据库失败: " + ex.getMessage());
            }
        }
    }

    private void setupTestData() {
        // 创建一些测试原料
        Ingredient ingredient1 = createTestIngredient(0, "鸡蛋");
        Ingredient ingredient2 = createTestIngredient(0, "鸡肉");
        Ingredient ingredient3 = createTestIngredient(0, "大米");

        ingredientDao.addIngredient(ingredient1);
        ingredientDao.addIngredient(ingredient2);
        ingredientDao.addIngredient(ingredient3);
    }

    private Ingredient createTestIngredient(int id, String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        return ingredient;
    }

    @Test
    void testAddIngredientSuccess() {
        // Given - 使用时间戳生成唯一的原料名称
        String uniqueName = "测试原料_" + System.currentTimeMillis();
        Ingredient ingredient = createTestIngredient(0, uniqueName);

        // When
        boolean result = ingredientDao.addIngredient(ingredient);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testAddIngredientFailure() {
        // Given - 先添加一个原料，再添加同名原料，测试UNIQUE约束失败
        Ingredient ingredient1 = createTestIngredient(0, "测试原料");
        Ingredient ingredient2 = createTestIngredient(0, "测试原料");

        // 添加第一个原料，应该成功
        boolean result1 = ingredientDao.addIngredient(ingredient1);
        assertThat(result1).isTrue();

        // 添加同名原料，应该失败
        boolean result2 = ingredientDao.addIngredient(ingredient2);

        // Then
        assertThat(result2).isFalse();
    }

    @Test
    void testGetIngredientByIdSuccess() {
        // Given
        setupTestData();
        // 获取第一个添加的原料ID（假设ID为1）
        Ingredient AddedIngredient = ingredientDao.getIngredientByName("鸡蛋");

        // When
        Ingredient result = ingredientDao.getIngredientById(AddedIngredient.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(AddedIngredient.getId());
        assertThat(result.getName()).isEqualTo("鸡蛋");
    }

    @Test
    void testGetIngredientByIdNotFound() {
        // Given
        int ingredientId = 99999;

        // When
        Ingredient result = ingredientDao.getIngredientById(ingredientId);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetIngredientByNameSuccess() {
        // Given
        setupTestData();
        String name = "鸡蛋";

        // When
        Ingredient result = ingredientDao.getIngredientByName(name);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
    }

    @Test
    void testGetIngredientByNameNotFound() {
        // Given
        String name = "不存在的原料名称";

        // When
        Ingredient result = ingredientDao.getIngredientByName(name);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetAllIngredients() {
        // Given
        setupTestData();

        // When
        List<Ingredient> result = ingredientDao.getAllIngredients();

        // Then
        assertThat(result).hasSize(3);
        // 验证所有预期的原料都存在（按名称排序）
        List<String> ingredientNames = result.stream().map(Ingredient::getName).toList();
        assertThat(ingredientNames).contains("大米", "鸡蛋", "鸡肉");
    }

    @Test
    void testSearchIngredients() {
        // Given
        setupTestData();
        String keyword = "鸡";

        // When
        List<Ingredient> result = ingredientDao.searchIngredients(keyword);

        // Then
        assertThat(result).hasSize(2); // 鸡蛋 and 鸡肉
    }

    @Test
    void testSearchIngredientsNoResults() {
        // Given
        String keyword = "不可能存在的关键词123456";

        // When
        List<Ingredient> result = ingredientDao.searchIngredients(keyword);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateIngredientSuccess() {
        // Given - 先添加一个测试原料进行更新
        String uniqueName = "测试原料_" + System.currentTimeMillis();
        Ingredient testIngredient = createTestIngredient(0, uniqueName);
        boolean added = ingredientDao.addIngredient(testIngredient);
        assertThat(added).isTrue();

        // 获取刚添加的原料的ID（会自动生成）
        Ingredient addedIngredient = ingredientDao.getIngredientByName(uniqueName);
        assertThat(addedIngredient).isNotNull();

        String newName = "更新测试_" + System.currentTimeMillis();
        addedIngredient.setName(newName);

        // When
        boolean result = ingredientDao.updateIngredient(addedIngredient);

        // Then
        assertThat(result).isTrue();
        // 验证更新是否成功
        Ingredient updatedIngredient = ingredientDao.getIngredientById(addedIngredient.getId());
        assertThat(updatedIngredient).isNotNull();
        assertThat(updatedIngredient.getName()).isEqualTo(newName);
    }

    @Test
    void testUpdateIngredientFailure() {
        // Given
        Ingredient ingredient = createTestIngredient(99999, "不存在的原料"); // 假设ID不存在

        // When
        boolean result = ingredientDao.updateIngredient(ingredient);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testDeleteIngredientSuccess() {
        // Given - 先添加一个测试原料
        Ingredient testIngredient = createTestIngredient(0, "待删除的测试原料");
        boolean added = ingredientDao.addIngredient(testIngredient);
        assertThat(added).isTrue();

        // When
        boolean result = ingredientDao.deleteIngredient(testIngredient.getId());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testDeleteIngredientFailure() {
        // Given
        int ingredientId = 99999; // 假设此ID不存在

        // When
        boolean result = ingredientDao.deleteIngredient(ingredientId);

        // Then
        assertThat(result).isFalse();
    }
}
