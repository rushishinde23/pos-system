package com.zosh.service.impl;

import com.zosh.domain.UserRole;
import com.zosh.modal.User;
import com.zosh.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserImplementationTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserImplementation customUserImplementation;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("cashier@example.com");
        user.setPassword("encodedPassword");
        user.setRole(UserRole.ROLE_BRANCH_CASHIER);
    }

    @Test
    void loadUserByUsername_success_returnsUserDetailsWithAuthority() {
        when(userRepository.findByEmail("cashier@example.com")).thenReturn(user);

        UserDetails details = customUserImplementation.loadUserByUsername("cashier@example.com");

        assertNotNull(details);
        assertEquals("cashier@example.com", details.getUsername());
        assertEquals("encodedPassword", details.getPassword());
        assertTrue(details.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_BRANCH_CASHIER")));
    }

    @Test
    void loadUserByUsername_userNotFound_throwsException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> customUserImplementation.loadUserByUsername("missing@example.com"));
    }
}