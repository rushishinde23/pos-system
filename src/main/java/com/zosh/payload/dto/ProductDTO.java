package com.zosh.payload.dto;

import com.zosh.modal.Store;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class ProductDTO {

    private Long id;
    private String name;
    private String sku;
    private String description;
    private Double mrp;
    private  Double sellingPrice;
    private String brand;
    private String image;
    private Long categoryID;
    private CategoryDTO category;
    private Long storeID;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
