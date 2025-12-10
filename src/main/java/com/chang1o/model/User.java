package com.chang1o.model;

public class User {
    private int id;
    private String username;
    private String password;

    public User(){}

    public User(String username,String password){
        this.username = username;
        this.password = password;
    }

    public User(int id,String username,String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getUsername(){
        return  username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }


    @Override
    public String toString(){
        return "User{id=" + id + ", username='" + username + "'}'";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id == user.id &&
               java.util.Objects.equals(username, user.username) &&
               java.util.Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, username, password);
    }
}
