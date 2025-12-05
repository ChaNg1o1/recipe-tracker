package com.chang1o.service;

import com.chang1o.dao.RecipeDao;
import com.chang1o.dao.CategoryDao;
import com.chang1o.dao.IngredientDao;
import com.chang1o.dao.RecipeIngredientDao;
import com.chang1o.model.Recipe;
import com.chang1o.model.Category;
import com.chang1o.model.Ingredient;
import com.chang1o.model.RecipeIngredient;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;

public class RecipeService {

    private RecipeDao recipeDao;
    private CategoryDao categoryDao;
    private IngredientDao ingredientDao;
    private RecipeIngredientDao recipeIngredientDao;

    public RecipeService() {
        this.recipeDao = new RecipeDao();
        this.categoryDao = new CategoryDao();
        this.ingredientDao = new IngredientDao();
        this.recipeIngredientDao = new RecipeIngredientDao();
    }

    public RecipeResult addRecipe(String name, String instructions, int categoryId, int userId) {
        ValidationResult validation = validateRecipeInput(name, instructions, categoryId);
        if (!validation.isValid()) {
            return new RecipeResult(false, null, validation.getMessage());
        }

        if (!categoryDao.exists(categoryId)) {
            return new RecipeResult(false, null, "分类不存在，请选择有效的分类");
        }

        Recipe newRecipe = new Recipe(name, instructions, categoryId, userId);
        boolean success = recipeDao.addRecipe(newRecipe);

        if (success) {
            return new RecipeResult(true, newRecipe, "食谱添加成功！");
        } else {
            return new RecipeResult(false, null, "食谱添加失败，请稍后重试");
        }
    }

    public RecipeResult updateRecipe(int recipeId, String name, String instructions, int categoryId, int userId) {
        ValidationResult validation = validateRecipeInput(name, instructions, categoryId);
        if (!validation.isValid()) {
            return new RecipeResult(false, null, validation.getMessage());
        }

        Recipe existingRecipe = recipeDao.getRecipeById(recipeId);
        if (existingRecipe == null) {
            return new RecipeResult(false, null, "食谱不存在");
        }

        if (!categoryDao.exists(categoryId)) {
            return new RecipeResult(false, null, "分类不存在，请选择有效的分类");
        }

        existingRecipe.setName(name);
        existingRecipe.setInstructions(instructions);
        existingRecipe.setCategoryId(categoryId);

        boolean success = recipeDao.updateRecipe(existingRecipe);

        if (success) {
            return new RecipeResult(true, existingRecipe, "食谱更新成功！");
        } else {
            return new RecipeResult(false, null, "食谱更新失败，请稍后重试");
        }
    }

    public RecipeResult deleteRecipe(int recipeId, int userId) {
        Recipe existingRecipe = recipeDao.getRecipeById(recipeId);
        if (existingRecipe == null) {
            return new RecipeResult(false, null, "食谱不存在");
        }

        boolean success = recipeDao.deleteRecipe(recipeId);

        if (success) {
            return new RecipeResult(true, null, "食谱删除成功！");
        } else {
            return new RecipeResult(false, null, "食谱删除失败，请稍后重试");
        }
    }

    public Recipe getRecipeById(int recipeId) {
        Recipe recipe = recipeDao.getRecipeById(recipeId);
        if (recipe != null) {
            Category category = categoryDao.getCategoryById(recipe.getCategoryId());
            recipe.setCategory(category);

            List<RecipeIngredient> ingredients = recipeIngredientDao.getIngredientsByRecipeId(recipeId);
            recipe.setIngredients(ingredients);
        }
        return recipe;
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = recipeDao.getAllRecipes();
        loadRecipeDetails(recipes);
        return recipes;
    }

    public List<Recipe> getRecipesByCategory(int categoryId) {
        List<Recipe> recipes = recipeDao.getRecipesByCategory(categoryId);
        loadRecipeDetails(recipes);
        return recipes;
    }

    public List<Recipe> searchRecipes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllRecipes();
        }

        List<Recipe> recipes = recipeDao.searchRecipes(keyword.trim());
        loadRecipeDetails(recipes);
        return recipes;
    }

    public List<Recipe> getRecipesByUser(int userId) {
        List<Recipe> recipes = recipeDao.getRecipesByUser(userId);
        loadRecipeDetails(recipes);
        return recipes;
    }

    public RecipeResult addRecipeWithIngredients(String name, String instructions, int categoryId, int userId, List<RecipeIngredient> ingredients) {
        ValidationResult validation = validateRecipeInput(name, instructions, categoryId);
        if (!validation.isValid()) {
            return new RecipeResult(false, null, validation.getMessage());
        }

        if (!categoryDao.exists(categoryId)) {
            return new RecipeResult(false, null, "分类不存在，请选择有效的分类");
        }

        Recipe newRecipe = new Recipe(name, instructions, categoryId, userId);
        boolean success = recipeDao.addRecipe(newRecipe);

        if (!success) {
            return new RecipeResult(false, null, "食谱添加失败，请稍后重试");
        }

        if (ingredients != null && !ingredients.isEmpty()) {
            for (RecipeIngredient ingredient : ingredients) {
                ingredient.setRecipeId(newRecipe.getId());
            }

            boolean ingredientsAdded = recipeIngredientDao.addRecipeIngredients(newRecipe.getId(), ingredients);
            if (!ingredientsAdded) {
                System.err.println("食材添加失败，但食谱已创建");
            }
        }

        return new RecipeResult(true, newRecipe, "食谱添加成功！");
    }

    public RecipeResult updateRecipeWithIngredients(int recipeId, String name, String instructions, int categoryId, int userId, List<RecipeIngredient> ingredients) {
        ValidationResult validation = validateRecipeInput(name, instructions, categoryId);
        if (!validation.isValid()) {
            return new RecipeResult(false, null, validation.getMessage());
        }

        Recipe existingRecipe = recipeDao.getRecipeById(recipeId);
        if (existingRecipe == null) {
            return new RecipeResult(false, null, "食谱不存在");
        }

        if (!categoryDao.exists(categoryId)) {
            return new RecipeResult(false, null, "分类不存在，请选择有效的分类");
        }

        existingRecipe.setName(name);
        existingRecipe.setInstructions(instructions);
        existingRecipe.setCategoryId(categoryId);

        boolean success = recipeDao.updateRecipe(existingRecipe);

        if (!success) {
            return new RecipeResult(false, null, "食谱更新失败，请稍后重试");
        }

        if (ingredients != null) {
            for (RecipeIngredient ingredient : ingredients) {
                ingredient.setRecipeId(recipeId);
            }

            boolean ingredientsUpdated = recipeIngredientDao.updateRecipeIngredients(recipeId, ingredients);
            if (!ingredientsUpdated) {
                System.err.println("食材更新失败，但食谱已更新");
            }
        }

        return new RecipeResult(true, existingRecipe, "食谱更新成功！");
    }

    private void loadRecipeDetails(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            Category category = categoryDao.getCategoryById(recipe.getCategoryId());
            recipe.setCategory(category);

            List<RecipeIngredient> ingredients = recipeIngredientDao.getIngredientsByRecipeId(recipe.getId());
            recipe.setIngredients(ingredients);
        }
    }

    private ValidationResult validateRecipeInput(String name, String instructions, int categoryId) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "食谱名称不能为空");
        }

        if (name.length() < 2 || name.length() > 100) {
            return new ValidationResult(false, "食谱名称长度必须在2-100个字符之间");
        }

        if (instructions == null || instructions.trim().isEmpty()) {
            return new ValidationResult(false, "制作步骤不能为空");
        }

        if (instructions.length() < 10) {
            return new ValidationResult(false, "制作步骤至少需要10个字符");
        }

        if (categoryId <= 0) {
            return new ValidationResult(false, "请选择有效的分类");
        }

        return new ValidationResult(true, "验证通过");
    }

    public static class RecipeResult {
        private boolean success;
        private Recipe recipe;
        private String message;

        public RecipeResult(boolean success, Recipe recipe, String message) {
            this.success = success;
            this.recipe = recipe;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public Recipe getRecipe() {
            return recipe;
        }

        public String getMessage() {
            return message;
        }
    }

    private static class ValidationResult {
        private boolean valid;
        private String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

}
