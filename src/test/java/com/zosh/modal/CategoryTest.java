package com.zosh.modal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testCategoryEntity(){

        Store store = new Store();

        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .store(store)
                .build();


        assertEquals(1L, category.getId());
        assertEquals("Electronics", category.getName());
        assertEquals(store, category.getStore());


        category.setName("Food");

        assertEquals("Food", category.getName());
    }
}