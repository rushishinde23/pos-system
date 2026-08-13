package com.zosh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zosh.payload.dto.CategoryDTO;
import com.zosh.service.CategoryService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
        objectMapper = new ObjectMapper();

        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Electronics");
    }

    @Nested
    @DisplayName("POST /api/categories")
    class CreateCategory {

        @Test
        @DisplayName("Should create category and return 200 OK with created payload")
        void createCategory_Success() throws Exception {
            // ARRANGE
            when(categoryService.createCategory(any(CategoryDTO.class))).thenReturn(categoryDTO);

            // ACT & ASSERT
            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Electronics"));

            verify(categoryService, times(1)).createCategory(any(CategoryDTO.class));
        }

        @Test
        @DisplayName("Should propagate exception when category creation fails")
        void createCategory_ThrowsException() throws Exception {
            // ARRANGE
            when(categoryService.createCategory(any(CategoryDTO.class)))
                    .thenThrow(new Exception("Category already exists"));

            // ACT & ASSERT
            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDTO)))
            );

            assertEquals("Category already exists", exception.getCause().getMessage());
            verify(categoryService, times(1)).createCategory(any(CategoryDTO.class));
        }
    }

    @Nested
    @DisplayName("GET /api/categories/store/{storeID}")
    class GetCategoriesByStoreID {

        @Test
        @DisplayName("Should return list of categories for given store ID")
        void getCategoriesByStoreID_Success() throws Exception {
            // ARRANGE
            List<CategoryDTO> categoryList = List.of(categoryDTO);
            when(categoryService.getCategoriesByStore(100L)).thenReturn(categoryList);

            // ACT & ASSERT
            mockMvc.perform(get("/api/categories/store/100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("Electronics"));

            verify(categoryService, times(1)).getCategoriesByStore(100L);
        }
    }

    @Nested
    @DisplayName("PUT /api/categories/{id}")
    class UpdateCategory {

        @Test
        @DisplayName("Should update category and return 200 OK")
        void updateCategory_Success() throws Exception {
            // ARRANGE
            when(categoryService.updateCategory(eq(1L), any(CategoryDTO.class))).thenReturn(categoryDTO);

            // ACT & ASSERT
            mockMvc.perform(put("/api/categories/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(categoryDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Electronics"));

            verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/categories/{id}")
    class DeleteCategory {

        @Test
        @DisplayName("Should invoke deleteCategory on service and return ApiResponse message")
        void deleteCategory_Success() throws Exception {
            // ARRANGE
            doNothing().when(categoryService).deleteCategory(1L);

            // ACT & ASSERT
            mockMvc.perform(delete("/api/categories/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Category deleted successfully"));

            verify(categoryService, times(1)).deleteCategory(1L);
        }

        @Test
        @DisplayName("Should propagate exception when category deletion fails")
        void deleteCategory_NotFound_ThrowsException() throws Exception {
            // ARRANGE
            doThrow(new Exception("Category not found with id 99"))
                    .when(categoryService).deleteCategory(99L);

            // ACT & ASSERT
            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(delete("/api/categories/99"))
            );

            assertEquals("Category not found with id 99", exception.getCause().getMessage());
            verify(categoryService, times(1)).deleteCategory(99L);
        }
    }
}