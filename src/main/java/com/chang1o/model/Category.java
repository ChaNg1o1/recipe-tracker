package com.chang1o.model;

public class Category {
    private int id;
    private String name;

    public Category(){}

    public Category(String name){
        this.name = name;
    }

    public Category(int id,String name){
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
        return "Category{id" + id + ",name" + name + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return id == category.id && java.util.Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name);
    }

}
