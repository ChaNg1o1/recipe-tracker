package com.chang1o.service;

import com.chang1o.dao.PantryDao;
import com.chang1o.dao.IngredientDao;
import com.chang1o.model.PantryItem;
import com.chang1o.model.Ingredient;

import java.time.LocalDate;
import java.util.List;

public class PantryService {

    private PantryDao pantryDao;
    private IngredientDao ingredientDao;

    public PantryService() {
        this.pantryDao = new PantryDao();
        this.ingredientDao = new IngredientDao();
    }

    public PantryResult addPantryItem(int userId, String ingredientName, String quantity, LocalDate expiryDate) {
        ValidationResult validation = validatePantryInput(ingredientName, quantity);
        if (!validation.isValid()) {
            return new PantryResult(false, null, validation.getMessage());
        }

        Ingredient ingredient = ingredientDao.getIngredientByName(ingredientName);
        if (ingredient == null) {
            ingredient = new Ingredient(ingredientName);
            boolean ingredientAdded = ingredientDao.addIngredient(ingredient);
            if (!ingredientAdded) {
                return new PantryResult(false, null, "添加原料失败，请稍后重试");
            }
        }

        PantryItem newItem = new PantryItem(userId, ingredient.getId(), quantity, expiryDate);
        boolean success = pantryDao.addPantryItem(newItem);

        if (success) {
            newItem.setIngredient(ingredient);
            return new PantryResult(true, newItem, "库存物品添加成功！");
        } else {
            return new PantryResult(false, null, "库存物品添加失败，请稍后重试");
        }
    }

    public PantryResult updatePantryItem(int itemId, int userId, String quantity, LocalDate expiryDate) {
        ValidationResult validation = validatePantryInput("temp", quantity);
        if (!validation.isValid()) {
            return new PantryResult(false, null, validation.getMessage());
        }

        PantryItem existingItem = pantryDao.getPantryItemById(itemId);
        if (existingItem == null) {
            return new PantryResult(false, null, "库存物品不存在");
        }

        if (existingItem.getUserId() != userId) {
            return new PantryResult(false, null, "您没有权限修改这个库存物品");
        }

        existingItem.setQuantity(quantity);
        existingItem.setExpiryDate(expiryDate);

        boolean success = pantryDao.updatePantryItem(existingItem);

        if (success) {
            Ingredient ingredient = ingredientDao.getIngredientById(existingItem.getIngredientId());
            existingItem.setIngredient(ingredient);
            return new PantryResult(true, existingItem, "库存物品更新成功！");
        } else {
            return new PantryResult(false, null, "库存物品更新失败，请稍后重试");
        }
    }

    public PantryResult deletePantryItem(int itemId, int userId) {
        PantryItem existingItem = pantryDao.getPantryItemById(itemId);
        if (existingItem == null) {
            return new PantryResult(false, null, "库存物品不存在");
        }

        if (existingItem.getUserId() != userId) {
            return new PantryResult(false, null, "您没有权限删除这个库存物品");
        }

        boolean success = pantryDao.deletePantryItem(itemId);

        if (success) {
            return new PantryResult(true, null, "库存物品删除成功！");
        } else {
            return new PantryResult(false, null, "库存物品删除失败，请稍后重试");
        }
    }

    public PantryItem getPantryItemById(int itemId) {
        PantryItem item = pantryDao.getPantryItemById(itemId);
        if (item != null) {
            Ingredient ingredient = ingredientDao.getIngredientById(item.getIngredientId());
            item.setIngredient(ingredient);
        }
        return item;
    }

    public List<PantryItem> getPantryItemsByUser(int userId) {
        List<PantryItem> items = pantryDao.getPantryItemsByUser(userId);
        for (PantryItem item : items) {
            Ingredient ingredient = ingredientDao.getIngredientById(item.getIngredientId());
            item.setIngredient(ingredient);
        }
        return items;
    }

    public List<PantryItem> getExpiringItems(int userId, int days) {
        List<PantryItem> items = pantryDao.getExpiringItems(userId, days);
        for (PantryItem item : items) {
            Ingredient ingredient = ingredientDao.getIngredientById(item.getIngredientId());
            item.setIngredient(ingredient);
        }
        return items;
    }

    public List<PantryItem> getExpiredItems(int userId) {
        List<PantryItem> items = pantryDao.getExpiredItems(userId);
        for (PantryItem item : items) {
            Ingredient ingredient = ingredientDao.getIngredientById(item.getIngredientId());
            item.setIngredient(ingredient);
        }
        return items;
    }

    public boolean hasExpiringItems(int userId, int days) {
        return !getExpiringItems(userId, days).isEmpty();
    }

    private ValidationResult validatePantryInput(String ingredientName, String quantity) {
        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            return new ValidationResult(false, "原料名称不能为空");
        }

        if (ingredientName.length() < 1 || ingredientName.length() > 100) {
            return new ValidationResult(false, "原料名称长度必须在1-100个字符之间");
        }

        if (quantity == null || quantity.trim().isEmpty()) {
            return new ValidationResult(false, "数量不能为空");
        }

        if (quantity.length() > 50) {
            return new ValidationResult(false, "数量描述太长，最多50个字符");
        }

        return new ValidationResult(true, "验证通过");
    }

    public static class PantryResult {
        private boolean success;
        private PantryItem item;
        private String message;

        public PantryResult(boolean success, PantryItem item, String message) {
            this.success = success;
            this.item = item;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public PantryItem getItem() {
            return item;
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
