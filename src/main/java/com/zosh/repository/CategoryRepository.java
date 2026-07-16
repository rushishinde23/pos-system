package com.zosh.repository;

import com.zosh.modal.Category;
import com.zosh.payload.dto.CategoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByStoreId(Long storeID);
}
