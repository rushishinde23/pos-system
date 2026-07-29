package com.zosh.service.impl;

import com.zosh.modal.Category;
import com.zosh.modal.Product;
import com.zosh.modal.Store;
import com.zosh.modal.User;
import com.zosh.payload.dto.ProductDTO;
import com.zosh.repository.CategoryRepository;
import com.zosh.repository.ProductRepository;
import com.zosh.repository.StoreRepository;
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
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductDTO productDTO;
    private Store store;
    private Category category;
    private User user;

    @BeforeEach
    void setUp() {
        store = new Store();
        store.setId(1L);
        category = Category.builder().name("Snacks").build();
        user = new User();

        productDTO = new ProductDTO();
        productDTO.setName("Chips");
        productDTO.setDescription("Salty chips");
        productDTO.setSku("SKU-1");
        productDTO.setStoreID(1L);
        productDTO.setCategoryID(1L);
        productDTO.setSellingPrice(2.5);
        productDTO.setBrand("Lays");
    }

    @Test
    void createProduct_success() throws Exception {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductDTO result = productService.createProduct(productDTO, user);

        assertEquals("Chips", result.getName());
    }

    @Test
    void createProduct_storeNotFound_throws() {
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> productService.createProduct(productDTO, user));
    }

    @Test
    void createProduct_categoryNotFound_throws() {
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> productService.createProduct(productDTO, user));
    }

    @Test
    void updateProduct_withCategory_success() throws Exception {
        Product existing = new Product();
        existing.setSellingPrice(1.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductDTO result = productService.updateProduct(1L, productDTO, user);

        assertNotNull(result);
        assertEquals("Chips", existing.getName());
    }

    @Test
    void updateProduct_withoutCategoryId_success() throws Exception {
        productDTO.setCategoryID(null);
        Product existing = new Product();
        existing.setCategory(category);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductDTO result = productService.updateProduct(1L, productDTO, user);

        assertNotNull(result);
        verify(categoryRepository, never()).findById(anyLong());
    }

    @Test
    void updateProduct_notFound_throws() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> productService.updateProduct(99L, productDTO, user));
    }

    @Test
    void deleteProduct_success() throws Exception {
        Product existing = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        productService.deleteProduct(1L, user);

        verify(productRepository).delete(existing);
    }

    @Test
    void deleteProduct_notFound_throws() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> productService.deleteProduct(99L, user));
    }

    @Test
    void getProductsByStoreID_returnsList() {
        Product p1 = new Product();
        p1.setCategory(category);
        Product p2 = new Product();
        p2.setCategory(category);
        when(productRepository.findByStoreId(1L)).thenReturn(Arrays.asList(p1, p2));
        List<ProductDTO> result = productService.getProductsByStoreID(1L);
        assertEquals(2, result.size());
    }

    @Test
    void searchByKeyword_returnsList() {
        Product p1 = new Product();
        p1.setCategory(category);
        when(productRepository.searchByKeyword(1L, "chips")).thenReturn(List.of(p1));
        List<ProductDTO> result = productService.searchByKeyword(1L, "chips");
        assertEquals(1, result.size());
    }
}