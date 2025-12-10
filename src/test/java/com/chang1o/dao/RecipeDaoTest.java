package com.chang1o.dao;

import com.chang1o.model.Category;
import com.chang1o.model.Recipe;
import com.chang1o.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RecipeDao单元测试
 * 使用真实的数据库连接进行测试，确保数据库状态正确隔离
 */
class RecipeDaoTest {

    private RecipeDao recipeDao;
    private UserDao userDao;
    private CategoryDao categoryDao;

    @BeforeEach
    void setUp() {
        // 设置测试数据库配置
        System.setProperty("DB_CONFIG", "test-database.properties");
        // 重置DBUtil实例以强制重新加载测试配置
        com.chang1o.util.DBUtil.resetInstance();
        recipeDao = new RecipeDao();
        userDao = new UserDao();
        categoryDao = new CategoryDao();
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
        // 创建测试用户
        User user1 = new User();
        user1.setUsername("testuser");
        user1.setPassword("password123");
        userDao.addUser(user1);

        User user2 = new User();
        user2.setUsername("user2");
        user2.setPassword("password456");
        userDao.addUser(user2);

        // 创建测试分类
        Category cat1 = new Category();
        cat1.setName("川菜");
        categoryDao.addCategory(cat1);

        Category cat2 = new Category();
        cat2.setName("粤菜");
        categoryDao.addCategory(cat2);

        // 创建测试菜谱
        Recipe recipe1 = createTestRecipe(0, "宫保鸡丁", "经典川菜", 1, 1);
        Recipe recipe2 = createTestRecipe(0, "白切鸡", "广东菜", 2, 1);
        Recipe recipe3 = createTestRecipe(0, "川菜测试", "川菜指令", 1, 2);

        recipeDao.addRecipe(recipe1);
        recipeDao.addRecipe(recipe2);
        recipeDao.addRecipe(recipe3);
    }

    private Recipe createTestRecipe(int id, String name, String instructions, int categoryId, int userId) {
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setName(name);
        recipe.setInstructions(instructions);
        recipe.setCategoryId(categoryId);
        recipe.setUserId(userId);
        return recipe;
    }

    @Test
    void testAddRecipeSuccess() {
        // Given - Need proper setup with categories and users
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        userDao.addUser(user);

        Category category = new Category();
        category.setName("川菜");
        categoryDao.addCategory(category);

        Recipe recipe = createTestRecipe(0, "番茄炒蛋", "先炒番茄，再加鸡蛋", 1, 1);

        // When
        boolean result = recipeDao.addRecipe(recipe);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testAddRecipeFailure() {
        // Given - adding recipe without proper foreign keys should fail
        Recipe recipe = createTestRecipe(0, "失败食谱", "无效指令", 999, 999);

        // When
        boolean result = recipeDao.addRecipe(recipe);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testGetRecipeByIdSuccess() {
        // Given
        setupTestData();
        int recipeId = 1;

        // When
        Recipe result = recipeDao.getRecipeById(recipeId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("宫保鸡丁");
    }

    @Test
    void testGetRecipeByIdNotFound() {
        // Given
        int recipeId = 999;

        // When
        Recipe result = recipeDao.getRecipeById(recipeId);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetRecipeByNameSuccess() {
        // Given
        setupTestData();
        String name = "宫保鸡丁";

        // When
        Recipe result = recipeDao.getRecipeByName(name);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
    }

    @Test
    void testGetRecipeByNameNotFound() {
        // Given
        String name = "不存在的食谱";

        // When
        Recipe result = recipeDao.getRecipeByName(name);

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetAllRecipes() {
        // Given
        setupTestData();

        // When
        List<Recipe> result = recipeDao.getAllRecipes();

        // Then
        assertThat(result).hasSize(3);
        // 验证所有预期的菜谱都存在（顺序可能由数据库决定）
        List<String> recipeNames = result.stream().map(Recipe::getName).toList();
        assertThat(recipeNames).contains("宫保鸡丁", "白切鸡", "川菜测试");
    }

    @Test
    void testGetRecipesByCategory() {
        // Given
        setupTestData();
        int categoryId = 1; // 川菜

        // When
        List<Recipe> result = recipeDao.getRecipesByCategory(categoryId);

        // Then
        assertThat(result).hasSize(2); // 宫保鸡丁 and 川菜测试
        assertThat(result.get(0).getName()).isEqualTo("宫保鸡丁");
        assertThat(result.get(1).getName()).isEqualTo("川菜测试");
    }

    @Test
    void testGetRecipesByCategoryEmpty() {
        // Given
        int categoryId = 999;

        // When
        List<Recipe> result = recipeDao.getRecipesByCategory(categoryId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testSearchRecipes() {
        // Given
        setupTestData();
        String keyword = "鸡";

        // When
        List<Recipe> result = recipeDao.searchRecipes(keyword);

        // Then
        assertThat(result).hasSize(2); // 宫保鸡丁 and 白切鸡
    }

    @Test
    void testSearchRecipesNoResults() {
        // Given
        String keyword = "不存在的关键词";

        // When
        List<Recipe> result = recipeDao.searchRecipes(keyword);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testGetRecipesByUser() {
        // Given
        setupTestData();
        int userId = 1;

        // When
        List<Recipe> result = recipeDao.getRecipesByUser(userId);

        // Then
        assertThat(result).hasSize(2); // 宫保鸡丁 and 白切鸡
    }

    @Test
    void testGetRecipesByUserEmpty() {
        // Given
        int userId = 999;

        // When
        List<Recipe> result = recipeDao.getRecipesByUser(userId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void testUpdateRecipeSuccess() {
        // Given
        setupTestData();
        Recipe recipe = recipeDao.getRecipeById(1);
        recipe.setName("更新后的菜谱");

        // When
        boolean result = recipeDao.updateRecipe(recipe);

        // Then
        assertThat(result).isTrue();
        Recipe updated = recipeDao.getRecipeById(1);
        assertThat(updated.getName()).isEqualTo("更新后的菜谱");
    }

    @Test
    void testUpdateRecipeFailure() {
        // Given
        Recipe recipe = createTestRecipe(999, "不存在的菜谱", "指令", 1, 1);

        // When
        boolean result = recipeDao.updateRecipe(recipe);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testDeleteRecipeSuccess() {
        // Given
        setupTestData();

        // When
        boolean result = recipeDao.deleteRecipe(1);

        // Then
        assertThat(result).isTrue();
        assertThat(recipeDao.getRecipeById(1)).isNull();
    }

    @Test
    void testDeleteRecipeFailure() {
        // Given
        int recipeId = 999;

        // When
        boolean result = recipeDao.deleteRecipe(recipeId);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testExistsTrue() {
        // Given
        setupTestData();

        // When
        boolean result = recipeDao.exists(1);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testExistsFalse() {
        // Given
        int recipeId = 999;

        // When
        boolean result = recipeDao.exists(recipeId);

        // Then
        assertThat(result).isFalse();
    }
}
