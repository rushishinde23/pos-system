package com.zosh.service;

import com.zosh.exceptions.UserException;
import com.zosh.payload.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO dto) throws Exception;
    List<CategoryDTO> getCategoriesByStore(Long storeID);
    CategoryDTO updateCategory(Long id, CategoryDTO dto) throws Exception;
    void deleteCategory(Long id) throws Exception;
}
