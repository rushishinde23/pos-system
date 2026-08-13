package com.zosh.payload.dto;

import com.zosh.modal.Store;
import com.zosh.modal.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchDTO {

    private Long id;
    private String name;
    private String address;
    private String pnone;
    private String email;
    private List<String> workingDays;
    private LocalTime closeTime;
    private LocalTime openTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Storedto store;
    private Long storeID;
    private UserDto manager;
}
