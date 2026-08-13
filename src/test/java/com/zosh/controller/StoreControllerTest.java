package com.zosh.controller;

import com.zosh.domain.StoreStatus;
import com.zosh.exceptions.UserException;
import com.zosh.modal.User;
import com.zosh.payload.dto.Storedto;
import com.zosh.payload.responce.ApiResponse;
import com.zosh.service.StoreService;
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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

    @Mock
    private StoreService storeService;

    @Mock
    private UserService userService;

    @InjectMocks
    private StoreController storeController;

    private User mockUser;
    private Storedto mockStoredto;
    private final String jwtToken = "Bearer mock-jwt-token";

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockStoredto = new Storedto();
    }

    @Nested
    @DisplayName("POST /api/stores - createStore")
    class CreateStoreTests {

        @Test
        @DisplayName("Should create store successfully and return 200 OK")
        void createStore_Success() throws UserException {
            when(userService.getUserFromJwtToken(jwtToken)).thenReturn(mockUser);
            when(storeService.createStore(any(Storedto.class), eq(mockUser))).thenReturn(mockStoredto);

            ResponseEntity<Storedto> response = storeController.createStore(mockStoredto, jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockStoredto, response.getBody());

            verify(userService, times(1)).getUserFromJwtToken(jwtToken);
            verify(storeService, times(1)).createStore(mockStoredto, mockUser);
        }

        @Test
        @DisplayName("Should throw UserException when JWT resolution fails")
        void createStore_UserException() throws UserException {
            when(userService.getUserFromJwtToken(jwtToken))
                    .thenThrow(new UserException("Invalid user token"));

            UserException exception = assertThrows(UserException.class, () ->
                    storeController.createStore(mockStoredto, jwtToken)
            );

            assertEquals("Invalid user token", exception.getMessage());
            verify(userService, times(1)).getUserFromJwtToken(jwtToken);
            verifyNoInteractions(storeService);
        }
    }

    @Nested
    @DisplayName("GET /api/stores - getAllStore")
    class GetAllStoreTests {

        @Test
        @DisplayName("Should return list of all stores")
        void getAllStore_Success() throws UserException {
            List<Storedto> storeList = List.of(mockStoredto);
            when(storeService.getAllStores()).thenReturn(storeList);

            ResponseEntity<List<Storedto>> response = storeController.getAllStore(jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());

            verify(storeService, times(1)).getAllStores();
        }

        @Test
        @DisplayName("Should return empty list when no stores exist")
        void getAllStore_EmptyList() throws UserException {
            when(storeService.getAllStores()).thenReturn(Collections.emptyList());

            ResponseEntity<List<Storedto>> response = storeController.getAllStore(jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty());

            verify(storeService, times(1)).getAllStores();
        }
    }

    @Nested
    @DisplayName("GET /api/stores/employee - getStoreByEmployee")
    class GetStoreByEmployeeTests {

        @Test
        @DisplayName("Should return store for logged in employee")
        void getStoreByEmployee_Success() throws UserException {
            when(storeService.getStoreByEmployee()).thenReturn(mockStoredto);

            ResponseEntity<Storedto> response = storeController.getStoreByEmployee(jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockStoredto, response.getBody());

            verify(storeService, times(1)).getStoreByEmployee();
        }
    }

    @Nested
    @DisplayName("PUT /api/stores/{id} - updateStore")
    class UpdateStoreTests {

        @Test
        @DisplayName("Should update store successfully")
        void updateStore_Success() throws Exception {
            Long storeId = 1L;
            when(storeService.updateStore(eq(storeId), any(Storedto.class))).thenReturn(mockStoredto);

            ResponseEntity<Storedto> response = storeController.updateStore(storeId, mockStoredto);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockStoredto, response.getBody());

            verify(storeService, times(1)).updateStore(storeId, mockStoredto);
        }
    }

    @Nested
    @DisplayName("DELETE /api/stores/{id} - deleteStore")
    class DeleteStoreTests {

        @Test
        @DisplayName("Should delete store and return success ApiResponse")
        void deleteStore_Success() throws Exception {
            Long storeId = 1L;
            doNothing().when(storeService).deleteStore(storeId);

            ResponseEntity<ApiResponse> response = storeController.deleteStore(storeId);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Store deleted Successfully", response.getBody().getMessage());

            verify(storeService, times(1)).deleteStore(storeId);
        }
    }

    @Nested
    @DisplayName("PUT /api/stores/{id}/moderate - moderateStore")
    class ModerateStoreTests {

        @Test
        @DisplayName("Should update store status during moderation")
        void moderateStore_Success() throws Exception {
            Long storeId = 1L;
            StoreStatus status = StoreStatus.PENDING;

            when(storeService.moderateStore(storeId, status)).thenReturn(mockStoredto);

            ResponseEntity<Storedto> response = storeController.moderateStore(storeId, status, mockStoredto);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockStoredto, response.getBody());

            verify(storeService, times(1)).moderateStore(storeId, status);
        }
    }

    @Nested
    @DisplayName("GET /api/stores/{id} - getStoreBYId")
    class GetStoreByIdTests {

        @Test
        @DisplayName("Should return store by ID using gerStoreById service call")
        void getStoreById_Success() throws Exception {
            Long storeId = 1L;
            when(storeService.gerStoreById(storeId)).thenReturn(mockStoredto);

            ResponseEntity<Storedto> response = storeController.getStoreBYId(storeId, jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockStoredto, response.getBody());

            verify(storeService, times(1)).gerStoreById(storeId);
        }

        @Test
        @DisplayName("Should throw exception when store ID is not found")
        void getStoreById_NotFound() throws Exception {
            Long storeId = 999L;
            when(storeService.gerStoreById(storeId)).thenThrow(new Exception("Store not found"));

            Exception exception = assertThrows(Exception.class, () ->
                    storeController.getStoreBYId(storeId, jwtToken)
            );

            assertEquals("Store not found", exception.getMessage());
            verify(storeService, times(1)).gerStoreById(storeId);
        }
    }
}