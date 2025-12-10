package com.chang1o.model;
public class Ingredient {
    private int id;
    private String name;

    public Ingredient(){}

    public Ingredient(String name){
        this.name = name;
    }

    public Ingredient(int id,String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }


    @Override
    public String toString(){
        return "Ingredient{id=" + id + ",name=" + name + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ingredient ingredient = (Ingredient) obj;
        return id == ingredient.id && java.util.Objects.equals(name, ingredient.name);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name);
    }
}
