package com.zosh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zosh.exceptions.UserException;
import com.zosh.payload.dto.UserDto;
import com.zosh.payload.responce.AuthResponse;
import com.zosh.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private UserDto userDto;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        userDto = new UserDto();
        authResponse = new AuthResponse();
    }

    @Nested
    @DisplayName("POST /auth/signup")
    class SignupTests {

        @Test
        @DisplayName("Should return 200 OK and AuthResponse on successful signup")
        void signupHandler_Success() throws Exception {
            when(authService.signup(any(UserDto.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").exists());

            verify(authService, times(1)).signup(any(UserDto.class));
        }

        @Test
        @DisplayName("Should throw UserException when user already exists")
        void signupHandler_UserAlreadyExists_ThrowsException() throws UserException {
            when(authService.signup(any(UserDto.class)))
                    .thenThrow(new UserException("User already exists with given email"));

            // Catch the exception thrown by MockMvc execution
            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto)))
            );

            // Verify the root cause is UserException with the expected message
            assertEquals(UserException.class, exception.getCause().getClass());
            assertEquals("User already exists with given email", exception.getCause().getMessage());

            verify(authService, times(1)).signup(any(UserDto.class));
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        @DisplayName("Should return 200 OK and AuthResponse on valid credentials")
        void loginHandler_Success() throws Exception {
            when(authService.login(any(UserDto.class))).thenReturn(authResponse);

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").exists());

            verify(authService, times(1)).login(any(UserDto.class));
        }

        @Test
        @DisplayName("Should throw UserException on invalid credentials")
        void loginHandler_InvalidCredentials_ThrowsException() throws UserException {
            when(authService.login(any(UserDto.class)))
                    .thenThrow(new UserException("Invalid username or password"));

            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userDto)))
            );

            assertEquals(UserException.class, exception.getCause().getClass());
            assertEquals("Invalid username or password", exception.getCause().getMessage());

            verify(authService, times(1)).login(any(UserDto.class));
        }
    }
}