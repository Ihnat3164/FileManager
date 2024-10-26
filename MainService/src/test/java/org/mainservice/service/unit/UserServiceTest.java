package org.mainservice.service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mainservice.DTO.JwtRequest;
import org.mainservice.DTO.JwtResponse;
import org.mainservice.exception.UserAlreadyExistsException;
import org.mainservice.model.User;
import org.mainservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mainservice.service.MyUserDetailsService;
import org.mainservice.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.mainservice.component.JWTUtils;


import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MyUserDetailsService myUserDetailsService;

    @Mock
    private JWTUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        User user = new User();
        user.setEmail("existing@example.com");

        when(userRepository.findUserByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(user));
        verify(userRepository, times(1)).findUserByEmail(user.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCreateNewUser() {
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("password");

        when(userRepository.findUserByEmail(user.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword()))
                .thenReturn("encodedPassword");

        User createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertEquals("encodedPassword", createdUser.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldCreateAuthTokenSuccessfully() {
        JwtRequest authRequest = new JwtRequest("user@example.com", "password");
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(myUserDetailsService.loadUserByUsername(authRequest.getEmail()))
                .thenReturn(userDetails);

        when(jwtUtils.generateToken(userDetails))
                .thenReturn("jwtToken");

        JwtResponse response = userService.createAuthToken(authRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
    }
}
