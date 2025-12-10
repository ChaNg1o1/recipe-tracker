package com.chang1o.model;

public class RecipeIngredient {

    private int recipeId;
    private int ingredientId;
    private String quantity;
    private Ingredient ingredient;

    public RecipeIngredient() {
    }

    public RecipeIngredient(int recipeId, int ingredientId, String quantity) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        // Only update ingredientId if it's currently 0 (unset) and ingredient is not null
        if (ingredient != null && this.ingredientId == 0) {
            this.ingredientId = ingredient.getId();
        }
        // When setting to null, preserve the existing ingredientId
    }

    @Override
    public String toString() {
        return "RecipeIngredient{" +
                "recipeId=" + recipeId +
                ", ingredientId=" + ingredientId +
                ", quantity='" + quantity + '\'' +
                ", ingredient=" + (ingredient != null ? ingredient.getName() : "null") +
                '}';
    }
}
