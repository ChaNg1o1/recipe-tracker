
package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.service.UserService;
import com.chang1o.ui.ConsoleUI;
import com.chang1o.ui.InputValidator;
import java.util.Scanner;

public class UserController extends BaseController {

    private final UserService userService;
    private User currentUser;

    public UserController() {
        this.userService = new UserService();
        this.currentUser = null;
    }

    public void register() {
        ConsoleUI.clearScreen();
        ConsoleUI.showTitleBox("用户注册");

        String username = InputValidator.getValidUsername(sessionManager.getScanner());
        String password = InputValidator.getValidPassword(sessionManager.getScanner());
        String confirmPassword = InputValidator.confirmPassword(sessionManager.getScanner(), password);

        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        if (result.isSuccess()) {
            ConsoleUI.showSuccess(result.getMessage());
        } else {
            ConsoleUI.showError(result.getMessage());
        }
    }

    public void login() {
        ConsoleUI.clearScreen();
        ConsoleUI.showTitleBox("用户登录");

        String username = InputValidator.getNonEmptyInput(sessionManager.getScanner(), "请输入用户名：");
        String password = InputValidator.getNonEmptyInput(sessionManager.getScanner(), "请输入密码：");

        UserService.LoginResult result = userService.login(username, password);

        if (result.isSuccess()) {
            currentUser = result.getUser();
            sessionManager.setCurrentUser(currentUser);  // 同步到SessionManager
            ConsoleUI.showSuccess(result.getMessage());
            System.out.println("欢迎回来，" + currentUser.getUsername() + "！");
        } else {
            ConsoleUI.showError(result.getMessage());
        }
    }

    public void logout() {
        if (currentUser != null) {
            ConsoleUI.showTitleBox("用户登出");
            System.out.println("再见，" + currentUser.getUsername() + "！");
            currentUser = null;
            sessionManager.setCurrentUser(null);  // 同步到SessionManager
            ConsoleUI.showSuccess("您已成功登出");
        } else {
            ConsoleUI.showInfo("您当前没有登录！");
        }
    }

    public boolean checkLogin() {
        if (currentUser == null) {
            System.out.println("请先登录！");
            return false;
        }
        return true;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
