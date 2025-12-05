package com.chang1o.ui;


import com.chang1o.model.User;
import java.time.LocalDateTime;
import java.util.Scanner;

public class ConsoleUI {

    public static void showWelcomeMessage() {
        System.out.println();
        System.out.println("Recipe Tracker v25.Nov27");
        System.out.println();
        System.out.println("  [功能特性]");
        System.out.println("  • 用户账户管理");
        System.out.println("  • 食谱创建与管理");
        System.out.println("  • 食品库存追踪");
        System.out.println("  • 健康数据记录");
        System.out.println("  • AI推荐");
        System.out.println("  • 数据导出备份");
        System.out.println();
        System.out.println();
    }

    public static void showExitMessage() {
        System.out.println();
        System.out.println("已退出！");
        System.out.println();
    }

    public static void showAbout() {
        System.out.println();
        System.out.println("编译时间");
        System.out.println();
        System.out.println("  RecipeTracker");
        System.out.println("  2025-11-27");
        System.out.println();
    }

    public static void showUserStatus(User currenUser){
        System.out.print("[当前用户]");
        System.out.print("用户名:" + currenUser.getUsername());
        System.out.println("登录时间:" + LocalDateTime.now());
    }

    public static void clearScreen(){
        System.out.println("\n\n\n\n\n\n\n\n\n\n");
    }

    public static void pause(){
        System.out.println("\n按回车继续...");
        try(java.util.Scanner scanner = new Scanner(System.in)){
            scanner.nextLine();
        }
    }

       public static void showSeparator() {
        System.out.println("──────────────────────────────────────────────────");
    }

     public static void showSuccess(String message) {
        System.out.println("[成功] " + message);
    }

    public static void showError(String message) {
        System.out.println("[错误] " + message);
    }

    public static void showWarning(String message) {
        System.out.println("[警告] " + message);
    }

    public static void showInfo(String message) {
        System.out.println("[提示] " + message);
    }
}