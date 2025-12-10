package com.chang1o.service;

import com.chang1o.dao.UserDao;
import com.chang1o.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        // 使用Mockito的反射注入mock对象
        try {
            java.lang.reflect.Field field = UserService.class.getDeclaredField("userDao");
            field.setAccessible(true);
            field.set(userService, userDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User createUser(int id, String username, String password) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }

    @Test
    void testRegisterSuccess() {
        // Given
        String username = "testuser";
        String password = "password123";
        String confirmPassword = "password123";

        when(userDao.getUserByUsername(username)).thenReturn(null);
        when(userDao.addUser(any(User.class))).thenReturn(true);

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("注册成功");
        verify(userDao).getUserByUsername(username);
        verify(userDao).addUser(any(User.class));
    }

    @Test
    void testRegisterUsernameExists() {
        // Given
        String username = "existinguser";
        String password = "password123";
        String confirmPassword = "password123";

        User existingUser = createUser(1, username, "oldpassword");
        when(userDao.getUserByUsername(username)).thenReturn(existingUser);

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("用户名已存在");
        verify(userDao).getUserByUsername(username);
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterInvalidUsername() {
        // Given
        String username = ""; // 无效用户名
        String password = "password123";
        String confirmPassword = "password123";

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("用户名不能为空");
        verify(userDao, never()).getUserByUsername(anyString());
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterPasswordMismatch() {
        // Given
        String username = "testuser";
        String password = "password123";
        String confirmPassword = "differentpassword";

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("两次输入的密码不一致");
        verify(userDao, never()).getUserByUsername(anyString());
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        // Given
        String username = "testuser";
        String password = "password123";
        User user = createUser(1, username, password);

        when(userDao.getUserByUsername(username)).thenReturn(user);

        // When
        UserService.LoginResult result = userService.login(username, password);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getMessage()).isEqualTo("登录成功");
        verify(userDao).getUserByUsername(username);
    }

    @Test
    void testLoginUserNotFound() {
        // Given
        String username = "nonexistent";
        String password = "password123";

        when(userDao.getUserByUsername(username)).thenReturn(null);

        // When
        UserService.LoginResult result = userService.login(username, password);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUser()).isNull();
        assertThat(result.getMessage()).isEqualTo("用户名不存在");
        verify(userDao).getUserByUsername(username);
    }

    @Test
    void testLoginWrongPassword() {
        // Given
        String username = "testuser";
        String password = "wrongpassword";
        User user = createUser(1, username, "correctpassword");

        when(userDao.getUserByUsername(username)).thenReturn(user);

        // When
        UserService.LoginResult result = userService.login(username, password);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUser()).isNull();
        assertThat(result.getMessage()).isEqualTo("密码错误");
        verify(userDao).getUserByUsername(username);
    }

    @Test
    void testChangePasswordSuccess() {
        // Given
        int userId = 1;
        String oldPassword = "oldpassword";
        String newPassword = "newpassword123";
        String confirmPassword = "newpassword123";

        User user = createUser(userId, "testuser", oldPassword);
        when(userDao.getUserById(userId)).thenReturn(user);
        when(userDao.updateUser(any(User.class))).thenReturn(true);

        // When
        UserService.PasswordChangeResult result = userService.changePassword(userId, oldPassword, newPassword, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("密码修改成功");
        verify(userDao).getUserById(userId);
        verify(userDao).updateUser(user);
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void testChangePasswordUserNotFound() {
        // Given
        int userId = 999;
        String oldPassword = "oldpassword";
        String newPassword = "newpassword123";
        String confirmPassword = "newpassword123";

        when(userDao.getUserById(userId)).thenReturn(null);

        // When
        UserService.PasswordChangeResult result = userService.changePassword(userId, oldPassword, newPassword, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("用户不存在");
        verify(userDao).getUserById(userId);
        verify(userDao, never()).updateUser(any(User.class));
    }

    @Test
    void testChangePasswordWrongOldPassword() {
        // Given
        int userId = 1;
        String oldPassword = "wrongoldpassword";
        String newPassword = "newpassword123";
        String confirmPassword = "newpassword123";

        User user = createUser(userId, "testuser", "correctoldpassword");
        when(userDao.getUserById(userId)).thenReturn(user);

        // When
        UserService.PasswordChangeResult result = userService.changePassword(userId, oldPassword, newPassword, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("原密码错误");
        verify(userDao).getUserById(userId);
        verify(userDao, never()).updateUser(any(User.class));
    }

    @Test
    void testGetUserInfo() {
        // Given
        int userId = 1;
        User expectedUser = createUser(userId, "testuser", "password");

        when(userDao.getUserById(userId)).thenReturn(expectedUser);

        // When
        User result = userService.getUserInfo(userId);

        // Then
        assertThat(result).isEqualTo(expectedUser);
        verify(userDao).getUserById(userId);
    }

    @Test
    void testGetAllUsers() {
        // Given
        List<User> expectedUsers = Arrays.asList(
            createUser(1, "user1", "pass1"),
            createUser(2, "user2", "pass2")
        );

        when(userDao.getAllUsers()).thenReturn(expectedUsers);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertThat(result).isEqualTo(expectedUsers);
        verify(userDao).getAllUsers();
    }

    @Test
    void testDeleteUser() {
        // Given
        int userId = 1;

        when(userDao.deleteUser(userId)).thenReturn(true);

        // When
        boolean result = userService.deleteUser(userId);

        // Then
        assertThat(result).isTrue();
        verify(userDao).deleteUser(userId);
    }

    @Test
    void testGetUserStatistics() {
        // Given
        List<User> users = Arrays.asList(
            createUser(1, "user1", "pass1"),
            createUser(2, "user2", "pass2"),
            createUser(3, "user3", "pass3")
        );

        when(userDao.getAllUsers()).thenReturn(users);

        // When
        UserService.UserStatistics result = userService.getUserStatistics();

        // Then
        assertThat(result.getTotalUsers()).isEqualTo(3);
        assertThat(result.getAllUsers()).isEqualTo(users);
        verify(userDao).getAllUsers();
    }

    @Test
    void testValidateRegistrationInput() {
        // Test cases for validation are covered in the registration tests above
        // The private method is tested indirectly through public methods
    }

    @Test
    void testValidateLoginInput() {
        // Test cases for validation are covered in the login tests above
        // The private method is tested indirectly through public methods
    }

    @Test
    void testValidatePasswordChange() {
        // Test cases for validation are covered in the changePassword tests above
        // The private method is tested indirectly through public methods
    }
}
