
package com.chang1o.model;

import java.time.LocalDate;

public class PantryItem {

    private int id;
    private int userId;
    private int ingredientId;
    private String quantity;
    private LocalDate expiryDate;
    private User user;
    private Ingredient ingredient;

    public PantryItem() {
    }

    public PantryItem(int userId, int ingredientId, String quantity, LocalDate expiryDate) {
        this.userId = userId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public PantryItem(int id, int userId, int ingredientId, String quantity, LocalDate expiryDate) {
        this.id = id;
        this.userId = userId;
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public int getDaysUntilExpiry() {
        if (expiryDate == null) {
            return Integer.MAX_VALUE;
        }
        return (int) (expiryDate.toEpochDay() - LocalDate.now().toEpochDay());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PantryItem pantryItem = (PantryItem) o;
        return id == pantryItem.id && 
               userId == pantryItem.userId && 
               ingredientId == pantryItem.ingredientId && 
               (quantity != null ? quantity.equals(pantryItem.quantity) : pantryItem.quantity == null) &&
               (expiryDate != null ? expiryDate.equals(pantryItem.expiryDate) : pantryItem.expiryDate == null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + userId;
        result = 31 * result + ingredientId;
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (expiryDate != null ? expiryDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PantryItem{" +
                "id=" + id +
                ", userId=" + userId +
                ", ingredientId=" + ingredientId +
                ", quantity='" + quantity + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
