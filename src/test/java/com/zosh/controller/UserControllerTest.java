package com.zosh.controller;

import com.zosh.exceptions.UserException;
import com.zosh.modal.User;
import com.zosh.payload.dto.UserDto;
import com.zosh.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User mockUser;
    private final String jwtToken = "Bearer mock-jwt-token";

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
    }

    @Nested
    @DisplayName("GET /api/users/profile - getUserProfile")
    class GetUserProfileTests {

        @Test
        @DisplayName("Should return UserDto for authenticated user profile")
        void getUserProfile_Success() throws UserException {
            when(userService.getUserFromJwtToken(jwtToken)).thenReturn(mockUser);

            ResponseEntity<UserDto> response = userController.getUserProfile(jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            verify(userService, times(1)).getUserFromJwtToken(jwtToken);
        }

        @Test
        @DisplayName("Should throw UserException when JWT resolution fails")
        void getUserProfile_UserException() throws UserException {
            when(userService.getUserFromJwtToken(jwtToken))
                    .thenThrow(new UserException("Invalid user token"));

            UserException exception = assertThrows(UserException.class, () ->
                    userController.getUserProfile(jwtToken)
            );

            assertEquals("Invalid user token", exception.getMessage());
            verify(userService, times(1)).getUserFromJwtToken(jwtToken);
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id} - getUserById")
    class GetUserByIdTests {

        @Test
        @DisplayName("Should return UserDto when valid user ID is provided")
        void getUserById_Success() throws Exception {
            Long userId = 1L;
            when(userService.getUserById(userId)).thenReturn(mockUser);

            ResponseEntity<UserDto> response = userController.getUserById(jwtToken, userId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            verify(userService, times(1)).getUserById(userId);
        }

        @Test
        @DisplayName("Should throw UserException when user with given ID does not exist")
        void getUserById_NotFound() throws Exception {
            Long userId = 999L;
            when(userService.getUserById(userId))
                    .thenThrow(new UserException("User not found with id: " + userId));

            UserException exception = assertThrows(UserException.class, () ->
                    userController.getUserById(jwtToken, userId)
            );

            assertEquals("User not found with id: 999", exception.getMessage());
            verify(userService, times(1)).getUserById(userId);
        }
    }
}