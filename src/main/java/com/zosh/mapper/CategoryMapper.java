package com.zosh.mapper;

import com.zosh.modal.Category;
import com.zosh.payload.dto.CategoryDTO;

public class CategoryMapper {
    public static CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .name(category.getName())
                .id(category.getId())
                .storeID(category.getStore()!=null?category.getStore().getId():null)
                .build();
    }
}
