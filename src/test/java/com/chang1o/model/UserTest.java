package com.chang1o.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    void testDefaultConstructor() {
        User defaultUser = new User();
        assertThat(defaultUser).isNotNull();
        assertThat(defaultUser.getId()).isEqualTo(0);
        assertThat(defaultUser.getUsername()).isNull();
        assertThat(defaultUser.getPassword()).isNull();
    }

    @Test
    void testConstructorWithUsernameAndPassword() {
        String username = "testuser";
        String password = "password123";
        
        User userWithParams = new User(username, password);
        
        assertThat(userWithParams).isNotNull();
        assertThat(userWithParams.getUsername()).isEqualTo(username);
        assertThat(userWithParams.getPassword()).isEqualTo(password);
        assertThat(userWithParams.getId()).isEqualTo(0);
    }

    @Test
    void testConstructorWithAllParameters() {
        int id = 1;
        String username = "testuser";
        String password = "password123";
        
        User userWithAllParams = new User(id, username, password);
        
        assertThat(userWithAllParams).isNotNull();
        assertThat(userWithAllParams.getId()).isEqualTo(id);
        assertThat(userWithAllParams.getUsername()).isEqualTo(username);
        assertThat(userWithAllParams.getPassword()).isEqualTo(password);
    }

    @Test
    void testSetAndGetId() {
        int expectedId = 123;
        user.setId(expectedId);
        assertThat(user.getId()).isEqualTo(expectedId);
    }

    @Test
    void testSetAndGetUsername() {
        String expectedUsername = "newuser";
        user.setUsername(expectedUsername);
        assertThat(user.getUsername()).isEqualTo(expectedUsername);
    }

    @Test
    void testSetAndGetPassword() {
        String expectedPassword = "newpassword";
        user.setPassword(expectedPassword);
        assertThat(user.getPassword()).isEqualTo(expectedPassword);
    }

    @Test
    void testToString() {
        user.setId(1);
        user.setUsername("testuser");
        
        String result = user.toString();
        
        assertThat(result).contains("User{id=1, username='testuser'}");
        assertThat(result).doesNotContain("password");
    }

    @Test
    void testToStringWithNullValues() {
        String result = user.toString();
        
        assertThat(result).contains("User{id=0, username='null'}");
    }

    @Test
    void testToStringWithEmptyUsername() {
        user.setId(2);
        user.setUsername("");
        
        String result = user.toString();
        
        assertThat(result).contains("User{id=2, username=''}");
    }

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User(1, "user1", "pass1");
        User user2 = new User(1, "user1", "pass1");
        User user3 = new User(2, "user2", "pass2");
        
        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1).isNotEqualTo(null);
        assertThat(user1).isNotEqualTo("not a user");
        
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void testIdBoundaryValues() {
        user.setId(Integer.MIN_VALUE);
        assertThat(user.getId()).isEqualTo(Integer.MIN_VALUE);
        
        user.setId(Integer.MAX_VALUE);
        assertThat(user.getId()).isEqualTo(Integer.MAX_VALUE);
        
        user.setId(0);
        assertThat(user.getId()).isEqualTo(0);
    }

    @Test
    void testUsernameEdgeCases() {
        String longUsername = "a".repeat(100);
        user.setUsername(longUsername);
        assertThat(user.getUsername()).isEqualTo(longUsername);
        
        String specialUsername = "user_123-ABC";
        user.setUsername(specialUsername);
        assertThat(user.getUsername()).isEqualTo(specialUsername);
    }

    @Test
    void testPasswordEdgeCases() {
        user.setPassword("");
        assertThat(user.getPassword()).isEqualTo("");
        
        String longPassword = "p".repeat(500);
        user.setPassword(longPassword);
        assertThat(user.getPassword()).isEqualTo(longPassword);
        
        String specialPassword = "P@ssw0rd!#$%^&*()";
        user.setPassword(specialPassword);
        assertThat(user.getPassword()).isEqualTo(specialPassword);
    }
}
