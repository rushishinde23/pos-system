package com.zosh.mapper;

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

        return userDto;
    }
}


