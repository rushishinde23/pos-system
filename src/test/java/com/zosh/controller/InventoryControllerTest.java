package com.zosh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zosh.payload.dto.InventoryDTO;
import com.zosh.payload.responce.ApiResponse;
import com.zosh.service.InventoryService;
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
class InventoryControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private InventoryDTO inventoryDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();

        inventoryDTO = new InventoryDTO();
        inventoryDTO.setId(100L);
        inventoryDTO.setBranchId(1L);
        inventoryDTO.setProductId(10L);
        inventoryDTO.setQuantity(50);
    }

    @Nested
    @DisplayName("POST /api/inventories")
    class CreateInventory {

        @Test
        @DisplayName("Should create inventory and return 200 OK")
        void create_Success() throws Exception {
            when(inventoryService.createInventory(any(InventoryDTO.class))).thenReturn(inventoryDTO);

            mockMvc.perform(post("/api/inventories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inventoryDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(100L))
                    .andExpect(jsonPath("$.branchId").value(1L))
                    .andExpect(jsonPath("$.productId").value(10L))
                    .andExpect(jsonPath("$.quantity").value(50));

            verify(inventoryService, times(1)).createInventory(any(InventoryDTO.class));
        }

        @Test
        @DisplayName("Should throw Exception when creation fails")
        void create_ThrowsException() throws Exception {
            when(inventoryService.createInventory(any(InventoryDTO.class)))
                    .thenThrow(new Exception("Branch not found"));

            Exception exception = assertThrows(Exception.class, () ->
                    mockMvc.perform(post("/api/inventories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inventoryDTO)))
            );

            assertEquals("Branch not found", exception.getCause().getMessage());
            verify(inventoryService, times(1)).createInventory(any(InventoryDTO.class));
        }
    }

    @Nested
    @DisplayName("PUT /api/inventories/{id}")
    class UpdateInventory {

        @Test
        @DisplayName("Should update inventory and return updated DTO")
        void update_Success() throws Exception {
            InventoryDTO updatedDto = new InventoryDTO();
            updatedDto.setId(100L);
            updatedDto.setBranchId(1L);
            updatedDto.setProductId(10L);
            updatedDto.setQuantity(100);

            when(inventoryService.updateInventory(eq(100L), any(InventoryDTO.class))).thenReturn(updatedDto);

            mockMvc.perform(put("/api/inventories/100")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(100L))
                    .andExpect(jsonPath("$.quantity").value(100));

            verify(inventoryService, times(1)).updateInventory(eq(100L), any(InventoryDTO.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/inventories/{id}")
    class DeleteInventory {

        @Test
        @DisplayName("Should delete inventory successfully")
        void delete_Success() throws Exception {
            doNothing().when(inventoryService).deleteInventory(100L);

            mockMvc.perform(delete("/api/inventories/100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Inventory deleted successfully"));

            verify(inventoryService, times(1)).deleteInventory(100L);
        }
    }

    @Nested
    @DisplayName("GET /api/inventories/branch/{branchId}/product/{productId}")
    class GetInventoryByProductAndBranch {

        @Test
        @DisplayName("Should fetch inventory by productId and branchId")
        void getInventoryByProductAndBranchId_Success() throws Exception {
            when(inventoryService.getInventoryByProductIdAndBranchId(10L, 1L)).thenReturn(inventoryDTO);

            // Path pattern: /branch/{branchId}/product/{productId} -> /branch/1/product/10
            mockMvc.perform(get("/api/inventories/branch/1/product/10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(100L))
                    .andExpect(jsonPath("$.branchId").value(1L))
                    .andExpect(jsonPath("$.productId").value(10L));

            verify(inventoryService, times(1)).getInventoryByProductIdAndBranchId(10L, 1L);
        }
    }

    @Nested
    @DisplayName("GET /api/inventories/branch/{branchId}")
    class GetInventoryByBranch {

        @Test
        @DisplayName("Should return list of inventories for a given branchId")
        void getInventoryByBranch_Success() throws Exception {
            List<InventoryDTO> list = List.of(inventoryDTO);
            when(inventoryService.getAllInventoryByBranchId(1L)).thenReturn(list);

            mockMvc.perform(get("/api/inventories/branch/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(100L))
                    .andExpect(jsonPath("$[0].branchId").value(1L));

            verify(inventoryService, times(1)).getAllInventoryByBranchId(1L);
        }
    }
}