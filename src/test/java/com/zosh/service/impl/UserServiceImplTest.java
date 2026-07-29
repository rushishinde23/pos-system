package com.zosh.service.impl;

import com.zosh.configuration.JwtProvider;
import com.zosh.exceptions.UserException;
import com.zosh.modal.User;
import com.zosh.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("john@example.com");
    }

    @Test
    void getUserFromJwtToken_success() throws UserException {
        when(jwtProvider.getEmailFromToken("tok")).thenReturn("john@example.com");
        when(userRepository.findByEmail("john@example.com")).thenReturn(user);

        User result = userService.getUserFromJwtToken("tok");

        assertEquals(user, result);
    }

    @Test
    void getUserFromJwtToken_notFound_throws() {
        when(jwtProvider.getEmailFromToken("tok")).thenReturn("missing@example.com");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(null);

        assertThrows(UserException.class, () -> userService.getUserFromJwtToken("tok"));
    }

    @Test
    void getCurrentUser_success() throws UserException {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn("john@example.com");
            when(context.getAuthentication()).thenReturn(authentication);
            mocked.when(SecurityContextHolder::getContext).thenReturn(context);
            when(userRepository.findByEmail("john@example.com")).thenReturn(user);

            User result = userService.getCurrentUser();

            assertEquals(user, result);
        }
    }

    @Test
    void getCurrentUser_notFound_throws() {
        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            SecurityContext context = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getName()).thenReturn("ghost@example.com");
            when(context.getAuthentication()).thenReturn(authentication);
            mocked.when(SecurityContextHolder::getContext).thenReturn(context);
            when(userRepository.findByEmail("ghost@example.com")).thenReturn(null);

            assertThrows(UserException.class, () -> userService.getCurrentUser());
        }
    }

    @Test
    void getUserByEmail_success() throws UserException {
        when(userRepository.findByEmail("john@example.com")).thenReturn(user);
        assertEquals(user, userService.getUserByEmail("john@example.com"));
    }

    @Test
    void getUserByEmail_notFound_throws() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(null);
        assertThrows(UserException.class, () -> userService.getUserByEmail("missing@example.com"));
    }

    @Test
    void getUserById_success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUserById(1L));
    }

    @Test
    void getUserById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> userService.getUserById(99L));
    }

    @Test
    void getAllUsers_returnsList() {
        List<User> users = Arrays.asList(user, new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
    }
}