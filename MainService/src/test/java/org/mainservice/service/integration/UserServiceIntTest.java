package org.mainservice.service.integration;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mainservice.exception.UserAlreadyExistsException;
import org.mainservice.model.User;
import org.mainservice.repository.UserRepository;
import org.mainservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ExtendWith(SpringExtension.class)
public class UserServiceIntTest {

    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static {
        postgres.start();
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeAll
    public static void setUp() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
        System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
    }

    @Test
    public void shouldCreateNewUser() {
        User user = new User();
        user.setName("Test");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setRole("USER");

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("testuser@example.com", createdUser.getEmail());
        assertTrue(passwordEncoder.matches("password", createdUser.getPassword()));

        User foundUser = userRepository.findUserByEmail("testuser@example.com").orElse(null);
        assertNotNull(foundUser);
        assertEquals("testuser@example.com", foundUser.getEmail());
    }

    @Test
    public void shouldThrowUserAlreadyExistsExceptionWhenUserExists() {
        User user = new User();
        user.setName("Test");
        user.setEmail("existinguser@example.com");
        user.setPassword("password");
        user.setRole("USER");
        userRepository.save(user);

        User newUser = new User();
        newUser.setEmail("existinguser@example.com");
        newUser.setPassword("newpassword");

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(newUser));
    }
}
