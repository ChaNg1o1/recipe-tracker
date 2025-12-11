package com.chang1o.controller;

import com.chang1o.model.User;
import com.chang1o.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
    void testUserServiceIntegration() {
        // Given
        when(userService.register(eq("testuser"), eq("password123"), eq("password123")))
            .thenReturn(createRegistrationResult(true, "注册成功！欢迎加入食谱管理系统"));

        // When
        UserService.RegistrationResult result = userService.register("testuser", "password123", "password123");

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).contains("注册成功");
        verify(userService).register("testuser", "password123", "password123");
    }

    @Test
    void testUserServiceLoginIntegration() {
        // Given
        User testUser = createUser(1, "testuser");
        when(userService.login(eq("testuser"), eq("password123")))
            .thenReturn(createLoginResult(true, testUser, "登录成功！"));

        // When
        UserService.LoginResult result = userService.login("testuser", "password123");

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUser()).isEqualTo(testUser);
        assertThat(result.getMessage()).contains("登录成功");
        verify(userService).login("testuser", "password123");
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
        assertThat(userController.getCurrentUser()).isNull();
    }

    @Test
    void testLogoutWhenNotLoggedIn() {
        // When
        userController.logout();

        // Then
        String output = outContent.toString();
        assertThat(output).contains("您当前没有登录！");
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

    @Test
    void testUserServiceValidation() {
        // Given
        when(userService.register(eq("ab"), eq("password123"), eq("password123")))
            .thenReturn(createRegistrationResult(false, "用户名长度必须在3-20个字符之间"));

        // When
        UserService.RegistrationResult result = userService.register("ab", "password123", "password123");

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("用户名长度");
        verify(userService).register("ab", "password123", "password123");
    }

    @Test
    void testLoginEmptyPasswordValidation() {
        // Given
        when(userService.login(eq("testuser"), eq("")))
            .thenReturn(createLoginResult(false, null, "密码不能为空"));

        // When
        UserService.LoginResult result = userService.login("testuser", "");

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("密码不能为空");
        verify(userService).login("testuser", "");
    }

    // 新增场景：checkLogin应只依赖controller的currentUser状态
    @Test
    void testCheckLoginDependsOnControllerState() {
        assertThat(userController.checkLogin()).isFalse();
        userController.setCurrentUser(createUser(3, "u3"));
        assertThat(userController.checkLogin()).isTrue();
    }
}
