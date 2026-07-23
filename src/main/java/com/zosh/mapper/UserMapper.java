package com.zosh.mapper;

import com.zosh.domain.UserRole;
import com.zosh.modal.User;
import com.zosh.payload.dto.UserDto;

public class UserMapper {
    public static UserDto toDTO(User savedUser) {

        UserDto userDto = new UserDto();
        userDto.setId(savedUser.getId());
        userDto.setFullName(savedUser.getFullName());
        userDto.setEmail(savedUser.getEmail());
        userDto.setRole(savedUser.getRole());
        userDto.setCreatedAt(savedUser.getCreatedAt());
        userDto.setLastLogin(savedUser.getLastLogin());
        userDto.setUpdatedAt(savedUser.getUpdatedAt());
        userDto.setPhone(savedUser.getPhone());
//        userDto.setStoreID(savedUser.getStore()!=null? savedUser.getStore().getId():null);
//        userDto.setBranchID(savedUser.getBranch()!=null? savedUser.getBranch().getId():null);
        if (savedUser.getStore() != null) {
            userDto.setStoreID(savedUser.getStore().getId());
        }

        if (savedUser.getBranch() != null) {
            userDto.setBranchID(savedUser.getBranch().getId());
        }

        return userDto;
    }

    public static User toEntity(UserDto userDto){
        User createdUser = new User();
        createdUser.setEmail(userDto.getEmail());
        createdUser.setFullName(userDto.getFullName());
        createdUser.setRole(userDto.getRole());
        createdUser.setCreatedAt(userDto.getCreatedAt());
        createdUser.setUpdatedAt(userDto.getUpdatedAt());
        createdUser.setLastLogin(userDto.getLastLogin());
        createdUser.setPhone(userDto.getPhone());
        createdUser.setPassword(userDto.getPassword());
        return createdUser;
    }
}


