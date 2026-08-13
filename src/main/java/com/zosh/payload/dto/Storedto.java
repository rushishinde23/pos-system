package com.zosh.payload.dto;

import com.zosh.domain.StoreStatus;
import com.zosh.modal.StoreContact;
import com.zosh.modal.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Storedto {

    private Long id;

    private String brand;


    private UserDto storeAdmin;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private  String description;

    private  String storeType;

    private StoreStatus status;

    private StoreContact contact ;

}
