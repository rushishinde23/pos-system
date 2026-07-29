package com.zosh.service.impl;

import com.zosh.domain.UserRole;
import com.zosh.exceptions.UserException;
import com.zosh.modal.Category;
import com.zosh.modal.Store;
import com.zosh.modal.User;
import com.zosh.payload.dto.CategoryDTO;
import com.zosh.repository.CategoryRepository;
import com.zosh.repository.StoreRepository;
import com.zosh.service.UserService;
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
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserService userService;
    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User adminUser;
    private Store store;
    private CategoryDTO dto;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setRole(UserRole.ROLE_STORE_ADMIN);

        store = new Store();
        store.setStoreAdmin(adminUser);

        dto = new CategoryDTO();
        dto.setStoreID(1L);
        dto.setName("Beverages");
    }

    @Test
    void createCategory_asStoreAdminOfSameStore_success() throws Exception {
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CategoryDTO result = categoryService.createCategory(dto);

        assertEquals("Beverages", result.getName());
    }

    @Test
    void createCategory_unauthorizedUser_throws() throws UserException {
        User otherAdmin = new User();
        otherAdmin.setId(2L);
        otherAdmin.setRole(UserRole.ROLE_STORE_ADMIN);

        when(userService.getCurrentUser()).thenReturn(otherAdmin);
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        assertThrows(Exception.class, () -> categoryService.createCategory(dto));
    }

    @Test
    void createCategory_storeNotFound_throws() throws UserException {
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> categoryService.createCategory(dto));
    }

    @Test
    void getCategoriesByStore_returnsList() {
        Category category = Category.builder().store(store).name("Snacks").build();
        when(categoryRepository.findByStoreId(1L)).thenReturn(Arrays.asList(category));

        List<CategoryDTO> result = categoryService.getCategoriesByStore(1L);

        assertEquals(1, result.size());
    }

    @Test
    void updateCategory_success() throws Exception {
        Category category = Category.builder().store(store).name("Old Name").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(categoryRepository.save(category)).thenReturn(category);

        CategoryDTO result = categoryService.updateCategory(1L, dto);

        assertEquals("Beverages", result.getName());
    }

    @Test
    void updateCategory_notFound_throws() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> categoryService.updateCategory(99L, dto));
    }

    @Test
    void deleteCategory_success() throws Exception {
        Category category = Category.builder().store(store).name("Snacks").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userService.getCurrentUser()).thenReturn(adminUser);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_notFound_throws() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> categoryService.deleteCategory(99L));
    }

    @Test
    void deleteCategory_unauthorized_throws() throws UserException {
        Category category = Category.builder().store(store).name("Snacks").build();
        User otherAdmin = new User();
        otherAdmin.setId(5L);
        otherAdmin.setRole(UserRole.ROLE_STORE_ADMIN);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userService.getCurrentUser()).thenReturn(otherAdmin);

        assertThrows(Exception.class, () -> categoryService.deleteCategory(1L));
    }
}