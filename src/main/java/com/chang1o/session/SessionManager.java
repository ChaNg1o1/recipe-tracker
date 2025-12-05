package com.chang1o.session;

import com.chang1o.model.User;
import com.mysql.cj.Session;

import java.util.Scanner;

public class SessionManager {
    private static SessionManager instance;
    private Scanner scanner;
    private User currentUser;
    private boolean running;

    private SessionManager(){
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
        this.running = true;
    }

    public static SessionManager getInstance(){
        //DCL双重锁
        if (instance == null) {
            synchronized(SessionManager.class){
                if (instance == null) {
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public Scanner getScanner(){
        return scanner;
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    public boolean isLoggedIn(){
        return currentUser != null;
    }

    public boolean checkLogin(){
        if (currentUser == null) {
            System.out.println("请先登录再使用系统");
            return false;
        }
        return true;
    }

    public void logout(){
        if (currentUser != null) {
            System.out.println("再见，" + currentUser.getUsername());
            currentUser = null;
            System.out.println("成功登出");
        }else{
            System.out.println("当前还没登陆！");
        }
    }

    public boolean isRuning(){
        return running;
    }

    public void stop(){
        this.running = false;
    }

    public void close(){
        if (scanner != null) {
            scanner.close();
        }
    }

    public String getInput(String prompt){
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
