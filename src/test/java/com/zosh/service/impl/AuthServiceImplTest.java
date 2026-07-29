package com.zosh.service.impl;

import com.zosh.configuration.JwtProvider;
import com.zosh.domain.UserRole;
import com.zosh.exceptions.UserException;
import com.zosh.modal.Branch;
import com.zosh.modal.Store;
import com.zosh.modal.User;
import com.zosh.payload.dto.UserDto;
import com.zosh.payload.responce.AuthResponse;
import com.zosh.repository.BranchRepository;
import com.zosh.repository.StoreRepository;
import com.zosh.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private CustomUserImplementation customUserImplementation;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserDto signupDto;

    @BeforeEach
    void setUp() {
        signupDto = new UserDto();
        signupDto.setEmail("new@example.com");
        signupDto.setPassword("plainPass");
        signupDto.setRole(UserRole.ROLE_BRANCH_CASHIER);
        signupDto.setFullName("New User");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void signup_success_returnsAuthResponse() throws UserException {
        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(passwordEncoder.encode("plainPass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtProvider.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = authService.signup(signupDto);

        assertEquals("jwt-token", response.getJwt());
        assertEquals("Registered Successfully", response.getMessage());
    }

    @Test
    void signup_emailAlreadyExists_throws() {
        when(userRepository.findByEmail("new@example.com")).thenReturn(new User());
        assertThrows(UserException.class, () -> authService.signup(signupDto));
    }

    @Test
    void signup_adminRole_throws() {
        signupDto.setRole(UserRole.ROLE_ADMIN);
        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        assertThrows(UserException.class, () -> authService.signup(signupDto));
    }

    @Test
    void signup_withBranchAndStore_success() throws UserException {
        signupDto.setBranchID(1L);
        signupDto.setStoreID(1L);

        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(branchRepository.findById(1L)).thenReturn(Optional.of(new Branch()));
        when(storeRepository.findById(1L)).thenReturn(Optional.of(new Store()));
        when(passwordEncoder.encode("plainPass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtProvider.generateToken(any())).thenReturn("jwt-token");

        AuthResponse response = authService.signup(signupDto);

        assertNotNull(response);
    }

    @Test
    void signup_branchNotFound_throws() {
        signupDto.setBranchID(1L);
        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(branchRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> authService.signup(signupDto));
    }

    @Test
    void login_success_returnsAuthResponse() throws UserException {
        UserDto loginDto = new UserDto();
        loginDto.setEmail("existing@example.com");
        loginDto.setPassword("plainPass");

        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("encoded");
        existingUser.setRole(UserRole.ROLE_BRANCH_CASHIER);

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "existing@example.com", "encoded",
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_BRANCH_CASHIER")));

        when(customUserImplementation.loadUserByUsername("existing@example.com")).thenReturn(springUser);
        when(passwordEncoder.matches("plainPass", "encoded")).thenReturn(true);
        when(jwtProvider.generateToken(any())).thenReturn("jwt-token");
        when(userRepository.findByEmail("existing@example.com")).thenReturn(existingUser);
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        AuthResponse response = authService.login(loginDto);

        assertEquals("jwt-token", response.getJwt());
        assertEquals("Login Successfully", response.getMessage());
    }

    @Test
    void login_wrongPassword_throws() {
        UserDto loginDto = new UserDto();
        loginDto.setEmail("existing@example.com");
        loginDto.setPassword("wrongPass");

        org.springframework.security.core.userdetails.User springUser =
                new org.springframework.security.core.userdetails.User(
                        "existing@example.com", "encoded",
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_BRANCH_CASHIER")));

        when(customUserImplementation.loadUserByUsername("existing@example.com")).thenReturn(springUser);
        when(passwordEncoder.matches("wrongPass", "encoded")).thenReturn(false);

        assertThrows(UserException.class, () -> authService.login(loginDto));
    }
}