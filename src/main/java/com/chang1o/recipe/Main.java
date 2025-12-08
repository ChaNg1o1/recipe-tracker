package com.chang1o.recipe;

import com.chang1o.controller.UserController;
import com.chang1o.controller.HealthController;
import com.chang1o.controller.RecipeController;
import com.chang1o.controller.PantryController;
import com.chang1o.controller.AIController;
import com.chang1o.controller.DataExportController;
import com.chang1o.ui.ConsoleUI;
import com.chang1o.ui.MenuManager;
import com.chang1o.session.SessionManager;

public class Main {

    private static SessionManager sessionManager = SessionManager.getInstance();
    private static UserController userController = new UserController();
    private static HealthController healthController = new HealthController();
    private static RecipeController recipeController = new RecipeController();
    private static PantryController pantryController = new PantryController();
    private static AIController aiController = new AIController();
    private static DataExportController dataExportController = new DataExportController();

    public static void main(String[] args) {
        initializeSystemData();
        ConsoleUI.showWelcomeMessage();

        while (sessionManager.isRunning()) {
            try {
                MenuManager.showMainMenu(sessionManager.getCurrentUser());
                String choice = sessionManager.getInput("请输入您的选择：");

                switch (choice) {
                    case "1":
                        userController.register();
                        break;
                    case "2":
                        userController.login();
                        break;
                    case "3":
                        if (sessionManager.isLoggedIn()) {
                            userController.logout();
                        } else {
                            ConsoleUI.showInfo("您当前没有登录！");
                        }
                        break;
                    case "4":
                        if (sessionManager.checkLogin()) {
                            recipeController.showMenu(sessionManager.getCurrentUser());
                        }
                        break;
                    case "5":
                        if (sessionManager.checkLogin()) {
                            pantryController.showMenu(sessionManager.getCurrentUser());
                        }
                        break;
                    case "6":
                        if (sessionManager.checkLogin()) {
                            healthController.showMenu(sessionManager.getCurrentUser());
                        }
                        break;
                    case "7":
                        if (sessionManager.checkLogin()) {
                            aiController.showMenu(sessionManager.getCurrentUser());
                        }
                        break;
                    case "8":
                        if (sessionManager.checkLogin()) {
                            dataExportController.exportData(sessionManager.getCurrentUser());
                        }
                        break;
                    case "9":
                        ConsoleUI.showAbout();
                        break;
                    case "0":
                        sessionManager.stop();
                        break;
                    default:
                        System.out.println("无效的选择，请重新输入！");
                        break;
                }
            } catch (Exception e) {
                System.out.println("系统错误：" + e.getMessage());
                e.printStackTrace();
            }

            if (sessionManager.isRunning()) {
                System.out.println("\n按回车键继续...");
                sessionManager.getScanner().nextLine();
            }
        }

        ConsoleUI.showExitMessage();
        sessionManager.close();
    }

    private static void initializeSystemData() {
        try {
            com.chang1o.util.DBUtil dbUtil = com.chang1o.util.DBUtil.getInstance();
            if (!dbUtil.testConnection()) {
                System.out.println("[错误] 数据库连接失败！");
                System.exit(1);
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("[错误] 系统初始化失败：" + e.getMessage());
            e.printStackTrace();
            System.out.println();
        }
    }
}
