package com.zosh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zosh.exceptions.UserException;
import com.zosh.payload.dto.BranchDTO;
import com.zosh.service.BranchService;
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
class BranchControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private BranchService branchService;

    @InjectMocks
    private BranchController branchController;

    private BranchDTO branchDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(branchController).build();
        objectMapper = new ObjectMapper();

        branchDTO = new BranchDTO();
        branchDTO.setId(1L);
        branchDTO.setName("Main Branch");
    }

    @Nested
    @DisplayName("POST /api/branches")
    class CreateBranch {

        @Test
        @DisplayName("Should create branch and return 200 OK")
        void createBranch_Success() throws Exception {
            when(branchService.createBranch(any(BranchDTO.class))).thenReturn(branchDTO);

            mockMvc.perform(post("/api/branches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(branchDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Main Branch"));

            verify(branchService, times(1)).createBranch(any(BranchDTO.class));
        }

        @Test
        @DisplayName("Should throw UserException when user is unauthorized/invalid")
        void createBranch_ThrowsUserException() throws UserException {
            when(branchService.createBranch(any(BranchDTO.class)))
                    .thenThrow(new UserException("User not authorized"));

            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/api/branches")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(branchDTO)))
            );

            assertEquals(UserException.class, exception.getCause().getClass());
            assertEquals("User not authorized", exception.getCause().getMessage());
        }
    }

    @Nested
    @DisplayName("GET /api/branches/{id}")
    class GetBranchById {

        @Test
        @DisplayName("Should return branch DTO when found")
        void getBranchById_Success() throws Exception {
            when(branchService.getBranchByID(1L)).thenReturn(branchDTO);

            mockMvc.perform(get("/api/branches/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Main Branch"));

            verify(branchService, times(1)).getBranchByID(1L);
        }

        @Test
        @DisplayName("Should throw Exception when branch not found")
        void getBranchById_NotFound_ThrowsException() throws Exception {
            when(branchService.getBranchByID(99L))
                    .thenThrow(new Exception("Branch not found"));

            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(get("/api/branches/99"))
            );

            assertEquals("Branch not found", exception.getCause().getMessage());
        }
    }

    @Nested
    @DisplayName("GET /api/branches/store/{storeID}")
    class GetAllBranchesByStoreId {

        @Test
        @DisplayName("Should return list of branches for given store ID")
        void getAllBranchesByStoreId_Success() throws Exception {
            List<BranchDTO> branches = List.of(branchDTO);
            when(branchService.getAllBranchesByStoreID(100L)).thenReturn(branches);

            mockMvc.perform(get("/api/branches/store/100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("Main Branch"));

            verify(branchService, times(1)).getAllBranchesByStoreID(100L);
        }
    }

    @Nested
    @DisplayName("PUT /api/branches/{id}")
    class UpdateBranch {

        @Test
        @DisplayName("Should update branch and return updated DTO")
        void updateBranch_Success() throws Exception {
            when(branchService.updateBranch(eq(1L), any(BranchDTO.class))).thenReturn(branchDTO);

            mockMvc.perform(put("/api/branches/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(branchDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L));

            verify(branchService, times(1)).updateBranch(eq(1L), any(BranchDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/branches/{id}")
    class DeleteBranch {

        @Test
        @DisplayName("Should delete branch and return success message")
        void deleteBranchById_Success() throws Exception {
            doNothing().when(branchService).deleteBranch(1L);

            mockMvc.perform(delete("/api/branches/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Branch deleted successfully"));

            verify(branchService, times(1)).deleteBranch(1L);
        }
    }
}