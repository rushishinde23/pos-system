package com.zosh.payload.dto;

import com.zosh.modal.Store;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDTO {

    private Long id;
    private String name;
    //private Store store;
    private Long storeID;
}
