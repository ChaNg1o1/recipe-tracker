
package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.model.Recipe;
import com.chang1o.model.Category;
import com.chang1o.service.RecipeService;
import com.chang1o.ui.ConsoleUI;
import com.chang1o.ui.MenuManager;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class RecipeController extends BaseController {

    private final RecipeService recipeService;

    public RecipeController() {
        this.recipeService = new RecipeService();
    }

    public void showMenu(User currentUser) {
        boolean inRecipeMenu = true;

        while (inRecipeMenu) {
            MenuManager.showRecipeMenu();

            System.out.print("请输入您的选择：");
            String choice = sessionManager.getScanner().nextLine().trim();

            switch (choice) {
                case "1":
                    addRecipe(currentUser);
                    break;
                case "2":
                    viewMyRecipes(currentUser);
                    break;
                case "3":
                    searchRecipes();
                    break;
                case "4":
                    browseByCategory();
                    break;
                case "5":
                    editRecipe(currentUser);
                    break;
                case "6":
                    deleteRecipe(currentUser);
                    break;
                case "7":
                    viewAllRecipes();
                    break;
                case "0":
                    inRecipeMenu = false;
                    break;
                default:
                    System.out.println("无效的选择，请重新输入！");
                    break;
            }

            if (inRecipeMenu) {
                ConsoleUI.pause(sessionManager.getScanner());
            }
        }
    }

    private void addRecipe(User currentUser) {
        System.out.println("\n添加新食谱");
        System.out.println("-".repeat(30));

        System.out.print("请输入食谱名称：");
        String name = sessionManager.getScanner().nextLine().trim();

        System.out.print("请输入制作说明：");
        String instructions = sessionManager.getScanner().nextLine().trim();

        List<Category> categories = getAllCategoriesForDisplay();
        if (categories.isEmpty()) {
            System.out.println("暂无可用的分类，请先添加分类！");
            return;
        }

        displayCategoriesForSelection(categories);

        System.out.print("请输入分类编号（1-" + categories.size() + "）：");
        String choiceStr = sessionManager.getScanner().nextLine().trim();

        try {
            int choice = Integer.parseInt(choiceStr);

            if (choice < 1 || choice > categories.size()) {
                System.out.println("无效的选择，请输入1-" + categories.size() + "之间的数字！");
                return;
            }

            Category selectedCategory = categories.get(choice - 1);
            int categoryId = selectedCategory.getId();

            RecipeService.RecipeResult result = recipeService.addRecipe(
                name, instructions, categoryId, currentUser.getId());

            if (result.isSuccess()) {
                System.out.println("食谱添加成功！");
                System.out.println("消息：" + result.getMessage());
            } else {
                System.out.println("添加失败：" + result.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字！");
        }
    }

    private void viewMyRecipes(User currentUser) {
        System.out.println("\n我的食谱");
        System.out.println("-".repeat(30));

        List<Recipe> recipes = recipeService.getRecipesByUser(currentUser.getId());

        if (recipes.isEmpty()) {
            System.out.println("您还没有添加任何食谱！");
        } else {
            System.out.println("您的食谱列表：");
            for (Recipe recipe : recipes) {
                System.out.println("名称：" + recipe.getName());
                System.out.println("分类：" + (recipe.getCategory() != null ? recipe.getCategory().getName() : "未分类"));
                System.out.println("说明：" + recipe.getInstructions());
                System.out.println("-".repeat(20));
            }
        }
    }

    private void searchRecipes() {
        System.out.println("\n搜索食谱");
        System.out.println("-".repeat(30));

        System.out.print("请输入搜索关键词：");
        String keyword = sessionManager.getScanner().nextLine().trim();

        List<Recipe> recipes = recipeService.searchRecipes(keyword);

        if (recipes.isEmpty()) {
            System.out.println("未找到相关食谱！");
        } else {
            System.out.println("搜索结果（" + recipes.size() + "个）：");
            for (Recipe recipe : recipes) {
                System.out.println("名称：" + recipe.getName());
                System.out.println("分类：" + (recipe.getCategory() != null ? recipe.getCategory().getName() : "未分类"));
                String instructions = recipe.getInstructions();
                System.out.println("说明：" + instructions.substring(0, Math.min(100, instructions.length())) + (instructions.length() > 100 ? "..." : ""));
                System.out.println("-".repeat(20));
            }
        }
    }

    private void browseByCategory() {
        System.out.println("\n按分类浏览");
        System.out.println("-".repeat(30));

        List<Category> categories = getAllCategoriesForDisplay();
        if (categories.isEmpty()) {
            System.out.println("暂无可用的分类！");
            return;
        }

        displayCategoriesForSelection(categories);

        System.out.print("请选择分类编号（1-" + categories.size() + "）：");
        String choiceStr = sessionManager.getScanner().nextLine().trim();

        try {
            int choice = Integer.parseInt(choiceStr);

            if (choice < 1 || choice > categories.size()) {
                System.out.println("无效的选择，请输入1-" + categories.size() + "之间的数字！");
                return;
            }

            Category selectedCategory = categories.get(choice - 1);
            int categoryId = selectedCategory.getId();

            List<Recipe> allRecipes = recipeService.getAllRecipes();
            List<Recipe> categoryRecipes = new ArrayList<>();

            for (Recipe recipe : allRecipes) {
                if (recipe.getCategoryId() == categoryId) {
                    categoryRecipes.add(recipe);
                }
            }

            if (categoryRecipes.isEmpty()) {
                System.out.println(selectedCategory.getName() + "分类下暂无食谱");
            } else {
                System.out.println("\n【" + selectedCategory.getName() + "】分类下的食谱：");
                for (int i = 0; i < categoryRecipes.size(); i++) {
                    Recipe recipe = categoryRecipes.get(i);
                    System.out.println((i + 1) + ". " + recipe.getName());
                    System.out.println("   说明：" + recipe.getInstructions().substring(0, Math.min(50, recipe.getInstructions().length())) + "...");
                    System.out.println("-".repeat(20));
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字！");
        }
    }

    private void editRecipe(User currentUser) {
        System.out.println("\n编辑食谱");
        System.out.println("-".repeat(30));

        List<Recipe> userRecipes = recipeService.getRecipesByUser(currentUser.getId());

        if (userRecipes.isEmpty()) {
            System.out.println("您还没有添加任何食谱！");
            return;
        }

        System.out.println("您的食谱列表：");
        for (int i = 0; i < userRecipes.size(); i++) {
            Recipe recipe = userRecipes.get(i);
            System.out.println((i + 1) + ". " + recipe.getName());
            System.out.println("   分类：" + (recipe.getCategory() != null ? recipe.getCategory().getName() : "未分类"));
            System.out.println("   说明：" + recipe.getInstructions().substring(0, Math.min(30, recipe.getInstructions().length())) + "...");
        }
        System.out.println("-".repeat(30));

        System.out.print("请选择要编辑的食谱编号（1-" + userRecipes.size() + "）：");
        String choiceStr = sessionManager.getScanner().nextLine().trim();

        try {
            int choice = Integer.parseInt(choiceStr);

            if (choice < 1 || choice > userRecipes.size()) {
                System.out.println("无效的选择，请输入1-" + userRecipes.size() + "之间的数字！");
                return;
            }

            Recipe recipe = userRecipes.get(choice - 1);
            int recipeId = recipe.getId();

            System.out.println("\n当前食谱信息：");
            System.out.println("名称：" + recipe.getName());
            System.out.println("说明：" + recipe.getInstructions());
            System.out.println("分类：" + (recipe.getCategory() != null ? recipe.getCategory().getName() : "未分类"));

            System.out.println("\n请输入新的信息（直接回车保持不变）：");

            System.out.print("新名称：");
            String newName = sessionManager.getScanner().nextLine().trim();
            if (!newName.isEmpty()) {
                if (newName.length() < 2 || newName.length() > 100) {
                    System.out.println("[错误] 食谱名称长度必须在2-100个字符之间");
                    return;
                }
                recipe.setName(newName);
            }

            System.out.print("新说明：");
            String newInstructions = sessionManager.getScanner().nextLine().trim();
            if (!newInstructions.isEmpty()) {
                if (newInstructions.length() < 10) {
                    System.out.println("[错误] 制作步骤至少需要10个字符");
                    return;
                }
                recipe.setInstructions(newInstructions);
            }

            System.out.print("是否修改分类？(y/n)：");
            String changeCategory = sessionManager.getScanner().nextLine().trim().toLowerCase();

            int newCategoryId = recipe.getCategoryId();

            if (changeCategory.equals("y") || changeCategory.equals("yes") || changeCategory.equals("是")) {
                List<Category> categories = getAllCategoriesForDisplay();
                if (categories.isEmpty()) {
                    System.out.println("暂无可用的分类，保持原分类！");
                } else {
                    displayCategoriesForSelection(categories);

                    System.out.print("请输入分类编号（1-" + categories.size() + "）：");
                    String categoryChoiceStr = sessionManager.getScanner().nextLine().trim();

                    try {
                        int categoryChoice = Integer.parseInt(categoryChoiceStr);

                        if (categoryChoice >= 1 && categoryChoice <= categories.size()) {
                            Category selectedCategory = categories.get(categoryChoice - 1);
                            newCategoryId = selectedCategory.getId();
                            System.out.println("[成功] 分类已更新为：" + selectedCategory.getName());
                        } else {
                            System.out.println("[错误] 无效的选择，保持原分类！");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[错误] 请输入有效的数字，保持原分类！");
                    }
                }
            }

            RecipeService.RecipeResult result = recipeService.updateRecipe(
                recipeId, recipe.getName(), recipe.getInstructions(),
                newCategoryId, currentUser.getId());

            if (result.isSuccess()) {
                System.out.println("[成功] 食谱更新成功！");
            } else {
                System.out.println("[错误] 更新失败：" + result.getMessage());
            }
        } catch (NumberFormatException e) {
            System.out.println("[错误] 请输入有效的数字！");
        }
    }

    private void deleteRecipe(User currentUser) {
        System.out.println("\n删除食谱");
        System.out.println("-".repeat(30));

        List<Recipe> userRecipes = recipeService.getRecipesByUser(currentUser.getId());

        if (userRecipes.isEmpty()) {
            System.out.println("您还没有添加任何食谱！");
            return;
        }

        System.out.println("您的食谱列表：");
        for (int i = 0; i < userRecipes.size(); i++) {
            Recipe recipe = userRecipes.get(i);
            System.out.println((i + 1) + ". " + recipe.getName());
            System.out.println("   说明：" + recipe.getInstructions().substring(0, Math.min(30, recipe.getInstructions().length())) + "...");
        }
        System.out.println("-".repeat(30));

        System.out.print("请选择要删除的食谱编号（1-" + userRecipes.size() + "）：");
        String choiceStr = sessionManager.getScanner().nextLine().trim();

        try {
            int choice = Integer.parseInt(choiceStr);

            if (choice < 1 || choice > userRecipes.size()) {
                System.out.println("无效的选择，请输入1-" + userRecipes.size() + "之间的数字！");
                return;
            }

            Recipe selectedRecipe = userRecipes.get(choice - 1);
            int recipeId = selectedRecipe.getId();

            System.out.print("确定要删除食谱《" + selectedRecipe.getName() + "》吗？(y/n)：");
            String confirm = sessionManager.getScanner().nextLine().trim().toLowerCase();

            if (confirm.equals("y") || confirm.equals("yes") || confirm.equals("是")) {
                RecipeService.RecipeResult result = recipeService.deleteRecipe(recipeId, currentUser.getId());

                if (result.isSuccess()) {
                    System.out.println("食谱删除成功！");
                } else {
                    System.out.println("删除失败：" + result.getMessage());
                }
            } else {
                System.out.println("已取消删除操作");
            }
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的数字！");
        }
    }

    private void viewAllRecipes() {
        System.out.println("\n所有食谱");
        System.out.println("-".repeat(30));

        List<Recipe> recipes = recipeService.getAllRecipes();

        if (recipes.isEmpty()) {
            System.out.println("暂无食谱！");
        } else {
            for (Recipe recipe : recipes) {
                System.out.println("名称：" + recipe.getName());
                System.out.println("分类ID：" + recipe.getCategoryId());
                System.out.println("说明：" + recipe.getInstructions());
                System.out.println("-".repeat(20));
            }
        }
    }

    private List<Category> getAllCategoriesForDisplay() {
        try {
            return queryAllCategories();
        } catch (Exception e) {
            System.out.println("获取分类列表失败：" + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Category> queryAllCategories() {
        List<Category> categories = new ArrayList<>();
        try {
            com.chang1o.dao.CategoryDao categoryDao = new com.chang1o.dao.CategoryDao();
            categories = categoryDao.getAllCategories();
        } catch (Exception e) {
            System.out.println("查询分类失败：" + e.getMessage());
            try {
                categories = getCategoriesFromRecipes();
            } catch (Exception ex) {
                System.out.println("从食谱获取分类也失败：" + ex.getMessage());
            }
        }
        return categories;
    }

    private List<Category> getCategoriesFromRecipes() {
        List<Category> categories = new ArrayList<>();
        try {
            categories = recipeService.getAllRecipes().stream()
                .map(Recipe::getCategory)
                .filter(category -> category != null)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            System.out.println("从食谱获取分类失败：" + e.getMessage());
        }
        return categories;
    }

    private void displayCategoriesForSelection(List<Category> categories) {
        System.out.println("\n请选择分类：");
        System.out.println("-".repeat(30));

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            System.out.println((i + 1) + ". " + category.getName());
        }

        System.out.println("-".repeat(30));
    }
}
