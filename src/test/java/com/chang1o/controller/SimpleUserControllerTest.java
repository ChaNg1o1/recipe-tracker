package com.chang1o.controller;

import com.chang1o.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleUserControllerTest {

    @Test
    void testCreateUser() {
        // Given
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        user.setPassword("password123");

        // When & Then
        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("password123");
    }

    @Test
    void testUserControllerCreation() {
        // When
        UserController controller = new UserController();

        // Then
        assertThat(controller).isNotNull();
        assertThat(controller.getCurrentUser()).isNull();
    }

    @Test
    void testSetCurrentUser() {
        // Given
        UserController controller = new UserController();
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");

        // When
        controller.setCurrentUser(user);

        // Then
        assertThat(controller.getCurrentUser()).isEqualTo(user);
    }

    @Test
    void testCheckLoginWhenNotLoggedIn() {
        // Given
        UserController controller = new UserController();

        // When
        boolean result = controller.checkLogin();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void testCheckLoginWhenLoggedIn() {
        // Given
        UserController controller = new UserController();
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");
        controller.setCurrentUser(user);

        // When
        boolean result = controller.checkLogin();

        // Then
        assertThat(result).isTrue();
    }
}