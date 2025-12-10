package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.service.UserService;
import com.chang1o.session.SessionManager;
import com.chang1o.ui.InputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;
    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        // 设置输出捕获
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        userController = new UserController();
        
        // 使用反射注入mock对象
        try {
            java.lang.reflect.Field field = UserController.class.getDeclaredField("userService");
            field.setAccessible(true);
            field.set(userController, userService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRegisterSuccess() {
        // Given
        String input = "testuser\npassword123\npassword123\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(getSessionManager().getScanner()).thenReturn(mockScanner);
        
        when(userService.register(eq("testuser"), eq("password123"), eq("password123")))
            .thenReturn(createRegistrationResult(true, "注册成功"));

        // When
        userController.register();

        // Then
        String output = outContent.toString();
        assertThat(output).contains("用户注册");
        assertThat(output).contains("注册成功");
        verify(userService).register("testuser", "password123", "password123");
    }

    @Test
    void testRegisterFailure() {
        // Given
        String input = "testuser\npassword123\npassword123\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(getSessionManager().getScanner()).thenReturn(mockScanner);
        
        when(userService.register(eq("testuser"), eq("password123"), eq("password123")))
            .thenReturn(createRegistrationResult(false, "用户名已存在"));

        // When
        userController.register();

        // Then
        String output = outContent.toString();
        assertThat(output).contains("用户注册");
        assertThat(output).contains("用户名已存在");
        verify(userService).register("testuser", "password123", "password123");
    }

    @Test
    void testLoginSuccess() {
        // Given
        User testUser = createUser(1, "testuser");
        String input = "testuser\npassword123\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(getSessionManager().getScanner()).thenReturn(mockScanner);
        
        when(userService.login(eq("testuser"), eq("password123")))
            .thenReturn(createLoginResult(true, testUser, "登录成功"));

        // When
        userController.login();

        // Then
        String output = outContent.toString();
        assertThat(output).contains("用户登录");
        assertThat(output).contains("登录成功");
        assertThat(output).contains("欢迎回来，testuser！");
        verify(userService).login("testuser", "password123");
        verify(getSessionManager()).setCurrentUser(eq(testUser));
        assertThat(userController.getCurrentUser()).isEqualTo(testUser);
    }

    @Test
    void testLoginFailure() {
        // Given
        String input = "testuser\nwrongpassword\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(getSessionManager().getScanner()).thenReturn(mockScanner);
        
        when(userService.login(eq("testuser"), eq("wrongpassword")))
            .thenReturn(createLoginResult(false, null, "密码错误"));

        // When
        userController.login();

        // Then
        String output = outContent.toString();
        assertThat(output).contains("用户登录");
        assertThat(output).contains("密码错误");
        verify(userService).login("testuser", "wrongpassword");
        verify(getSessionManager(), never()).setCurrentUser(any());
        assertThat(userController.getCurrentUser()).isNull();
    }

    @Test
    void testLogoutWhenLoggedIn() {
        // Given
        User testUser = createUser(1, "testuser");
        userController.setCurrentUser(testUser);

        // When
        userController.logout();

        // Then
        String output = outContent.toString();
        assertThat(output).contains("用户登出");
        assertThat(output).contains("再见，testuser！");
        assertThat(output).contains("您已成功登出");
        verify(getSessionManager()).setCurrentUser(eq(null));
        assertThat(userController.getCurrentUser()).isNull();
    }

    @Test
    void testLogoutWhenNotLoggedIn() {
        // When
        userController.logout();

        // Then
        String output = outContent.toString();
        assertThat(output).contains("您当前没有登录！");
        verify(getSessionManager(), never()).setCurrentUser(any());
    }

    @Test
    void testCheckLoginWhenLoggedIn() {
        // Given
        User testUser = createUser(1, "testuser");
        userController.setCurrentUser(testUser);

        // When
        boolean result = userController.checkLogin();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void testCheckLoginWhenNotLoggedIn() {
        // Given
        // userController.currentUser is null by default

        // When
        boolean result = userController.checkLogin();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testGetCurrentUser() {
        // Given
        User testUser = createUser(1, "testuser");
        userController.setCurrentUser(testUser);

        // When
        User result = userController.getCurrentUser();

        // Then
        assertThat(result).isEqualTo(testUser);
    }

    @Test
    void testSetCurrentUser() {
        // Given
        User testUser = createUser(1, "testuser");

        // When
        userController.setCurrentUser(testUser);

        // Then
        assertThat(userController.getCurrentUser()).isEqualTo(testUser);
    }

    private SessionManager getSessionManager() {
        try {
            java.lang.reflect.Field field = BaseController.class.getDeclaredField("sessionManager");
            field.setAccessible(true);
            return (SessionManager) field.get(userController);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User createUser(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return user;
    }

    private UserService.RegistrationResult createRegistrationResult(boolean success, String message) {
        return new UserService.RegistrationResult(success, message);
    }

    private UserService.LoginResult createLoginResult(boolean success, User user, String message) {
        UserService.LoginResult result = new UserService.LoginResult(success, user, message);
        return result;
    }

    // 新增场景：注册时用户名过短应提示错误
    @Test
    void testRegisterUsernameTooShort() {
        String input = "ab\npassword123\npassword123\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(getSessionManager().getScanner()).thenReturn(mockScanner);

        when(userService.register(eq("ab"), eq("password123"), eq("password123")))
            .thenReturn(createRegistrationResult(false, "用户名长度必须在3-20个字符之间"));

        userController.register();

        String output = outContent.toString();
        assertThat(output).contains("用户注册");
        assertThat(output).contains("用户名长度");
        verify(userService).register("ab", "password123", "password123");
    }

    // 新增场景：登录时空密码应提示错误
    @Test
    void testLoginEmptyPassword() {
        String input = "testuser\n\n"; // 空密码
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(getSessionManager().getScanner()).thenReturn(mockScanner);

        when(userService.login(eq("testuser"), eq("")))
            .thenReturn(createLoginResult(false, null, "密码不能为空"));

        userController.login();

        String output = outContent.toString();
        assertThat(output).contains("用户登录");
        assertThat(output).contains("密码不能为空");
        verify(userService).login("testuser", "");
        verify(getSessionManager(), never()).setCurrentUser(any());
    }

    // 新增场景：登录成功后SessionManager与controller状态一致
    @Test
    void testLoginSyncsSessionManagerAndController() {
        User testUser = createUser(2, "u2");
        String input = "u2\npass\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(getSessionManager().getScanner()).thenReturn(mockScanner);

        when(userService.login(eq("u2"), eq("pass")))
            .thenReturn(createLoginResult(true, testUser, "登录成功"));

        userController.login();

        verify(getSessionManager()).setCurrentUser(testUser);
        assertThat(userController.getCurrentUser()).isEqualTo(testUser);
    }

    // 新增场景：登出时多次调用不应抛异常且提示未登录
    @Test
    void testLogoutIdempotentWhenNotLoggedIn() {
        userController.logout();
        String first = outContent.toString();
        assertThat(first).contains("您当前没有登录！");

        outContent.reset();
        userController.logout();
        String second = outContent.toString();
        assertThat(second).contains("您当前没有登录！");
        verify(getSessionManager(), never()).setCurrentUser(any());
    }

    // 新增场景：checkLogin应只依赖controller的currentUser状态
    @Test
    void testCheckLoginDependsOnControllerState() {
        assertThat(userController.checkLogin()).isFalse();
        userController.setCurrentUser(createUser(3, "u3"));
        assertThat(userController.checkLogin()).isTrue();
    }
}
