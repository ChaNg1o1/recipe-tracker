package com.chang1o.service;

import com.chang1o.dao.UserDao;
import com.chang1o.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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

        when(userDao.isUsernameExists(username)).thenReturn(false);
        when(userDao.addUser(any(User.class))).thenReturn(true);

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("注册成功！欢迎加入食谱管理系统");
        verify(userDao).isUsernameExists(username);
        verify(userDao).addUser(any(User.class));
    }

    @Test
    void testRegisterUsernameExists() {
        // Given
        String username = "existinguser";
        String password = "password123";
        String confirmPassword = "password123";

        when(userDao.isUsernameExists(username)).thenReturn(true);

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("用户名已存在，请选择其他用户名");
        verify(userDao).isUsernameExists(username);
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

        when(userDao.isUsernameExists(username)).thenReturn(true);
        when(userDao.authenticate(username, password)).thenReturn(user);

        // When
        UserService.LoginResult result = userService.login(username, password);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getMessage()).isEqualTo("登录成功！");
        verify(userDao).isUsernameExists(username);
        verify(userDao).authenticate(username, password);
    }

    @Test
    void testLoginUserNotFound() {
        // Given
        String username = "nonexistent";
        String password = "password123";

        when(userDao.isUsernameExists(username)).thenReturn(false);

        // When
        UserService.LoginResult result = userService.login(username, password);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUser()).isNull();
        assertThat(result.getMessage()).isEqualTo("用户不存在，请检查用户名或先注册");
        verify(userDao).isUsernameExists(username);
    }

    @Test
    void testLoginWrongPassword() {
        // Given
        String username = "testuser";
        String password = "wrongpassword";

        when(userDao.isUsernameExists(username)).thenReturn(true);
        when(userDao.authenticate(username, password)).thenReturn(null);

        // When
        UserService.LoginResult result = userService.login(username, password);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUser()).isNull();
        assertThat(result.getMessage()).isEqualTo("密码错误，请重试");
        verify(userDao).isUsernameExists(username);
        verify(userDao).authenticate(username, password);
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
        assertThat(result.getMessage()).isEqualTo("密码修改成功！");
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

        when(userDao.countUsers()).thenReturn(3);
        when(userDao.getAllUsers()).thenReturn(users);

        // When
        UserService.UserStatistics result = userService.getUserStatistics();

        // Then
        assertThat(result.getTotalUsers()).isEqualTo(3);
        assertThat(result.getAllUsers()).isEqualTo(users);
        verify(userDao).countUsers();
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

    // Additional boundary condition tests for Task 3.1

    @Test
    void testRegisterUsernameTooShort() {
        // Given
        String username = "ab"; // 2 characters, below minimum of 3
        String password = "password123";
        String confirmPassword = "password123";

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("用户名长度必须在3-20个字符之间");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterUsernameTooLong() {
        // Given
        String username = "a".repeat(21); // 21 characters, above maximum of 20
        String password = "password123";
        String confirmPassword = "password123";

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("用户名长度必须在3-20个字符之间");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterUsernameMinimumLength() {
        // Given
        String username = "abc"; // 3 characters, minimum valid length
        String password = "password123";
        String confirmPassword = "password123";

        when(userDao.isUsernameExists(username)).thenReturn(false);
        when(userDao.addUser(any(User.class))).thenReturn(true);

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("注册成功！欢迎加入食谱管理系统");
        verify(userDao).isUsernameExists(username);
        verify(userDao).addUser(any(User.class));
    }

    @Test
    void testRegisterUsernameMaximumLength() {
        // Given
        String username = "a".repeat(20); // 20 characters, maximum valid length
        String password = "password123";
        String confirmPassword = "password123";

        when(userDao.isUsernameExists(username)).thenReturn(false);
        when(userDao.addUser(any(User.class))).thenReturn(true);

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("注册成功！欢迎加入食谱管理系统");
        verify(userDao).isUsernameExists(username);
        verify(userDao).addUser(any(User.class));
    }

    @Test
    void testRegisterUsernameInvalidCharacters() {
        // Given
        String username = "user@name"; // Contains invalid character @
        String password = "password123";
        String confirmPassword = "password123";

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("用户名只能包含字母、数字和下划线");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterUsernameWithValidSpecialCharacters() {
        // Given
        String username = "user_123"; // Contains valid characters: letters, numbers, underscore
        String password = "password123";
        String confirmPassword = "password123";

        when(userDao.isUsernameExists(username)).thenReturn(false);
        when(userDao.addUser(any(User.class))).thenReturn(true);

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("注册成功！欢迎加入食谱管理系统");
        verify(userDao).isUsernameExists(username);
        verify(userDao).addUser(any(User.class));
    }

    @Test
    void testRegisterPasswordTooShort() {
        // Given
        String username = "testuser";
        String password = "12345"; // 5 characters, below minimum of 6
        String confirmPassword = "12345";

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("密码长度至少为6个字符");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterPasswordMinimumLength() {
        // Given
        String username = "testuser";
        String password = "123456"; // 6 characters, minimum valid length
        String confirmPassword = "123456";

        when(userDao.isUsernameExists(username)).thenReturn(false);
        when(userDao.addUser(any(User.class))).thenReturn(true);

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).isEqualTo("注册成功！欢迎加入食谱管理系统");
        verify(userDao).isUsernameExists(username);
        verify(userDao).addUser(any(User.class));
    }

    @Test
    void testRegisterNullUsername() {
        // Given
        String username = null;
        String password = "password123";
        String confirmPassword = "password123";

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("用户名不能为空");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterNullPassword() {
        // Given
        String username = "testuser";
        String password = null;
        String confirmPassword = null;

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("密码不能为空");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterWhitespaceOnlyUsername() {
        // Given
        String username = "   "; // Only whitespace
        String password = "password123";
        String confirmPassword = "password123";

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("用户名不能为空");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterWhitespaceOnlyPassword() {
        // Given
        String username = "testuser";
        String password = "   "; // Only whitespace
        String confirmPassword = "   ";

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("密码不能为空");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).addUser(any(User.class));
    }

    @Test
    void testRegisterDatabaseFailure() {
        // Given
        String username = "testuser";
        String password = "password123";
        String confirmPassword = "password123";

        when(userDao.isUsernameExists(username)).thenReturn(false);
        when(userDao.addUser(any(User.class))).thenReturn(false); // Database failure

        // When
        UserService.RegistrationResult result = userService.register(username, password, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("注册失败，请稍后重试");
        verify(userDao).isUsernameExists(username);
        verify(userDao).addUser(any(User.class));
    }

    @Test
    void testLoginNullUsername() {
        // Given
        String username = null;
        String password = "password123";

        // When
        UserService.LoginResult result = userService.login(username, password);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUser()).isNull();
        assertThat(result.getMessage()).isEqualTo("用户名不能为空");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).authenticate(anyString(), anyString());
    }

    @Test
    void testLoginNullPassword() {
        // Given
        String username = "testuser";
        String password = null;

        // When
        UserService.LoginResult result = userService.login(username, password);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUser()).isNull();
        assertThat(result.getMessage()).isEqualTo("密码不能为空");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).authenticate(anyString(), anyString());
    }

    @Test
    void testLoginWhitespaceOnlyUsername() {
        // Given
        String username = "   "; // Only whitespace
        String password = "password123";

        // When
        UserService.LoginResult result = userService.login(username, password);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUser()).isNull();
        assertThat(result.getMessage()).isEqualTo("用户名不能为空");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).authenticate(anyString(), anyString());
    }

    @Test
    void testLoginWhitespaceOnlyPassword() {
        // Given
        String username = "testuser";
        String password = "   "; // Only whitespace

        // When
        UserService.LoginResult result = userService.login(username, password);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getUser()).isNull();
        assertThat(result.getMessage()).isEqualTo("密码不能为空");
        verify(userDao, never()).isUsernameExists(anyString());
        verify(userDao, never()).authenticate(anyString(), anyString());
    }

    @Test
    void testChangePasswordNewPasswordTooShort() {
        // Given
        int userId = 1;
        String oldPassword = "oldpassword";
        String newPassword = "12345"; // 5 characters, below minimum of 6
        String confirmPassword = "12345";

        User user = createUser(userId, "testuser", oldPassword);
        when(userDao.getUserById(userId)).thenReturn(user);

        // When
        UserService.PasswordChangeResult result = userService.changePassword(userId, oldPassword, newPassword, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("新密码长度至少为6个字符");
        verify(userDao).getUserById(userId);
        verify(userDao, never()).updateUser(any(User.class));
    }

    @Test
    void testChangePasswordNewPasswordMismatch() {
        // Given
        int userId = 1;
        String oldPassword = "oldpassword";
        String newPassword = "newpassword123";
        String confirmPassword = "differentpassword";

        User user = createUser(userId, "testuser", oldPassword);
        when(userDao.getUserById(userId)).thenReturn(user);

        // When
        UserService.PasswordChangeResult result = userService.changePassword(userId, oldPassword, newPassword, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("两次输入的新密码不一致");
        verify(userDao).getUserById(userId);
        verify(userDao, never()).updateUser(any(User.class));
    }

    @Test
    void testChangePasswordNullNewPassword() {
        // Given
        int userId = 1;
        String oldPassword = "oldpassword";
        String newPassword = null;
        String confirmPassword = null;

        User user = createUser(userId, "testuser", oldPassword);
        when(userDao.getUserById(userId)).thenReturn(user);

        // When
        UserService.PasswordChangeResult result = userService.changePassword(userId, oldPassword, newPassword, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("新密码不能为空");
        verify(userDao).getUserById(userId);
        verify(userDao, never()).updateUser(any(User.class));
    }

    @Test
    void testChangePasswordWhitespaceOnlyNewPassword() {
        // Given
        int userId = 1;
        String oldPassword = "oldpassword";
        String newPassword = "   "; // Only whitespace
        String confirmPassword = "   ";

        User user = createUser(userId, "testuser", oldPassword);
        when(userDao.getUserById(userId)).thenReturn(user);

        // When
        UserService.PasswordChangeResult result = userService.changePassword(userId, oldPassword, newPassword, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("新密码不能为空");
        verify(userDao).getUserById(userId);
        verify(userDao, never()).updateUser(any(User.class));
    }

    @Test
    void testChangePasswordDatabaseFailure() {
        // Given
        int userId = 1;
        String oldPassword = "oldpassword";
        String newPassword = "newpassword123";
        String confirmPassword = "newpassword123";

        User user = createUser(userId, "testuser", oldPassword);
        when(userDao.getUserById(userId)).thenReturn(user);
        when(userDao.updateUser(any(User.class))).thenReturn(false); // Database failure

        // When
        UserService.PasswordChangeResult result = userService.changePassword(userId, oldPassword, newPassword, confirmPassword);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).isEqualTo("密码修改失败，请稍后重试");
        verify(userDao).getUserById(userId);
        verify(userDao).updateUser(user);
    }

    @Test
    void testGetUserInfoNotFound() {
        // Given
        int userId = 999;

        when(userDao.getUserById(userId)).thenReturn(null);

        // When
        User result = userService.getUserInfo(userId);

        // Then
        assertThat(result).isNull();
        verify(userDao).getUserById(userId);
    }

    @Test
    void testDeleteUserNotFound() {
        // Given
        int userId = 999;

        when(userDao.deleteUser(userId)).thenReturn(false);

        // When
        boolean result = userService.deleteUser(userId);

        // Then
        assertThat(result).isFalse();
        verify(userDao).deleteUser(userId);
    }

    @Test
    void testGetUserStatisticsEmptyDatabase() {
        // Given
        List<User> emptyUsers = new ArrayList<>();

        when(userDao.countUsers()).thenReturn(0);
        when(userDao.getAllUsers()).thenReturn(emptyUsers);

        // When
        UserService.UserStatistics result = userService.getUserStatistics();

        // Then
        assertThat(result.getTotalUsers()).isEqualTo(0);
        assertThat(result.getAllUsers()).isEmpty();
        verify(userDao).countUsers();
        verify(userDao).getAllUsers();
    }
}
