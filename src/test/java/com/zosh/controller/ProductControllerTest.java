package com.zosh.controller;

import com.zosh.exceptions.UserException;
import com.zosh.modal.User;
import com.zosh.payload.dto.ProductDTO;
import com.zosh.payload.responce.ApiResponse;
import com.zosh.service.ProductService;
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
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProductController productController;

    private User mockUser;
    private ProductDTO mockProductDTO;
    private final String jwtToken = "Bearer mock-jwt-token";

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockProductDTO = new ProductDTO();
    }

    @Nested
    @DisplayName("POST /api/products - create")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully and return 200 OK")
        void create_Success() throws Exception {
            when(userService.getUserFromJwtToken(jwtToken)).thenReturn(mockUser);
            when(productService.createProduct(any(ProductDTO.class), eq(mockUser))).thenReturn(mockProductDTO);

            ResponseEntity<ProductDTO> response = productController.create(mockProductDTO, jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockProductDTO, response.getBody());

            verify(userService, times(1)).getUserFromJwtToken(jwtToken);
            verify(productService, times(1)).createProduct(mockProductDTO, mockUser);
        }

        @Test
        @DisplayName("Should throw UserException when JWT token is invalid")
        void create_InvalidJwtToken() throws Exception {
            when(userService.getUserFromJwtToken(jwtToken))
                    .thenThrow(new UserException("Invalid JWT token"));

            UserException exception = assertThrows(UserException.class, () ->
                    productController.create(mockProductDTO, jwtToken)
            );

            assertEquals("Invalid JWT token", exception.getMessage());
            verify(userService, times(1)).getUserFromJwtToken(jwtToken);
            verifyNoInteractions(productService);
        }
    }

    @Nested
    @DisplayName("GET /api/products/store/{storeID} - getByStoreID")
    class GetByStoreIDTests {

        @Test
        @DisplayName("Should return list of products for given store ID")
        void getByStoreID_Success() throws Exception {
            Long storeId = 101L;
            List<ProductDTO> productList = List.of(mockProductDTO);

            when(productService.getProductsByStoreID(storeId)).thenReturn(productList);

            ResponseEntity<List<ProductDTO>> response = productController.getByStoreID(storeId, jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
            assertEquals(mockProductDTO, response.getBody().get(0));

            verify(productService, times(1)).getProductsByStoreID(storeId);
        }

        @Test
        @DisplayName("Should return empty list when no products found for store")
        void getByStoreID_EmptyList() throws Exception {
            Long storeId = 101L;
            when(productService.getProductsByStoreID(storeId)).thenReturn(Collections.emptyList());

            ResponseEntity<List<ProductDTO>> response = productController.getByStoreID(storeId, jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertTrue(response.getBody().isEmpty());

            verify(productService, times(1)).getProductsByStoreID(storeId);
        }
    }

    @Nested
    @DisplayName("PATCH /api/products/{id} - update")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update product successfully and return updated ProductDTO")
        void update_Success() throws Exception {
            Long productId = 1L;
            when(userService.getUserFromJwtToken(jwtToken)).thenReturn(mockUser);
            when(productService.updateProduct(eq(productId), any(ProductDTO.class), eq(mockUser))).thenReturn(mockProductDTO);

            ResponseEntity<ProductDTO> response = productController.update(productId, mockProductDTO, jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(mockProductDTO, response.getBody());

            verify(userService, times(1)).getUserFromJwtToken(jwtToken);
            verify(productService, times(1)).updateProduct(productId, mockProductDTO, mockUser);
        }

        @Test
        @DisplayName("Should throw exception when product to update is not found")
        void update_ProductNotFound() throws Exception {
            Long productId = 999L;
            when(userService.getUserFromJwtToken(jwtToken)).thenReturn(mockUser);
            when(productService.updateProduct(eq(productId), any(ProductDTO.class), eq(mockUser)))
                    .thenThrow(new Exception("Product not found"));

            Exception exception = assertThrows(Exception.class, () ->
                    productController.update(productId, mockProductDTO, jwtToken)
            );

            assertEquals("Product not found", exception.getMessage());
            verify(userService, times(1)).getUserFromJwtToken(jwtToken);
            verify(productService, times(1)).updateProduct(productId, mockProductDTO, mockUser);
        }
    }

    @Nested
    @DisplayName("GET /api/products/store/{storeID}/search - searchByKeyword")
    class SearchByKeywordTests {

        @Test
        @DisplayName("Should return matching products for keyword search")
        void searchByKeyword_Success() throws Exception {
            Long storeId = 101L;
            String keyword = "laptop";
            List<ProductDTO> productList = List.of(mockProductDTO);

            when(productService.searchByKeyword(storeId, keyword)).thenReturn(productList);

            ResponseEntity<List<ProductDTO>> response = productController.searchByKeyword(storeId, keyword, jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());

            verify(productService, times(1)).searchByKeyword(storeId, keyword);
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id} - delete")
    class DeleteProductTests {

        @Test
        @DisplayName("Should delete product successfully and return success ApiResponse")
        void delete_Success() throws Exception {
            Long productId = 1L;
            when(userService.getUserFromJwtToken(jwtToken)).thenReturn(mockUser);
            doNothing().when(productService).deleteProduct(productId, mockUser);

            ResponseEntity<ApiResponse> response = productController.delete(productId, jwtToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("Product deleted successfully", response.getBody().getMessage());

            verify(userService, times(1)).getUserFromJwtToken(jwtToken);
            verify(productService, times(1)).deleteProduct(productId, mockUser);
        }

        @Test
        @DisplayName("Should throw exception when unauthorized to delete product")
        void delete_UnauthorizedUser() throws Exception {
            Long productId = 1L;
            when(userService.getUserFromJwtToken(jwtToken)).thenReturn(mockUser);
            doThrow(new Exception("User not authorized to delete this product"))
                    .when(productService).deleteProduct(productId, mockUser);

            Exception exception = assertThrows(Exception.class, () ->
                    productController.delete(productId, jwtToken)
            );

            assertEquals("User not authorized to delete this product", exception.getMessage());
            verify(userService, times(1)).getUserFromJwtToken(jwtToken);
            verify(productService, times(1)).deleteProduct(productId, mockUser);
        }
    }
}