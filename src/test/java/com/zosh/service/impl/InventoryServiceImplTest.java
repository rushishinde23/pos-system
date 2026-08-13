package com.zosh.service.impl;

import com.zosh.modal.Branch;
import com.zosh.modal.Category;
import com.zosh.modal.Inventory;
import com.zosh.modal.Product;
import com.zosh.payload.dto.InventoryDTO;
import com.zosh.repository.BranchRepository;
import com.zosh.repository.InventoryRepository;
import com.zosh.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Branch branch;
    private Product product;
    private InventoryDTO inventoryDTO;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        branch = new Branch();
        branch.setId(1L);
        product = new Product();
        product.setId(1L);
        product.setCategory(Category.builder().name("Snacks").build());

        inventoryDTO = new InventoryDTO();
        inventoryDTO.setBranchId(1L);
        inventoryDTO.setProductId(1L);
        inventoryDTO.setQuantity(50);

        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantity(50);
        inventory.setBranch(branch);
        inventory.setProduct(product);
    }

    @Test
    void createInventory_success() throws Exception {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        InventoryDTO result = inventoryService.createInventory(inventoryDTO);

        assertNotNull(result);
    }

    @Test
    void createInventory_branchNotFound_throws() {
        when(branchRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> inventoryService.createInventory(inventoryDTO));
    }

    @Test
    void createInventory_productNotFound_throws() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> inventoryService.createInventory(inventoryDTO));
    }

    @Test
    void updateInventory_success() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);

        InventoryDTO result = inventoryService.updateInventory(1L, inventoryDTO);

        assertNotNull(result);
        assertEquals(50, inventory.getQuantity());
    }

    @Test
    void updateInventory_notFound_throws() {
        when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> inventoryService.updateInventory(99L, inventoryDTO));
    }

    @Test
    void deleteInventory_success() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        inventoryService.deleteInventory(1L);
        verify(inventoryRepository).delete(inventory);
    }

    @Test
    void deleteInventory_notFound_throws() {
        when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> inventoryService.deleteInventory(99L));
    }

    @Test
    void getInventory_success() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        InventoryDTO result = inventoryService.getInventory(1L);
        assertNotNull(result);
    }

    @Test
    void getInventory_notFound_throws() {
        when(inventoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> inventoryService.getInventory(99L));
    }

    @Test
    void getInventoryByProductIdAndBranchId_returnsDto() {
        when(inventoryRepository.findByProductIdAndBranchId(1L, 1L)).thenReturn(inventory);
        InventoryDTO result = inventoryService.getInventoryByProductIdAndBranchId(1L, 1L);
        assertNotNull(result);
    }

    @Test
    void getAllInventoryByBranchId_returnsList() {
        Inventory second = new Inventory();
        second.setId(2L);
        second.setQuantity(10);
        second.setBranch(branch);
        second.setProduct(product);

        when(inventoryRepository.findByBranchId(1L)).thenReturn(Arrays.asList(inventory, second));
        List<InventoryDTO> result = inventoryService.getAllInventoryByBranchId(1L);
        assertEquals(2, result.size());
    }
}