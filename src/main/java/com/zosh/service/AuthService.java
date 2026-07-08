package com.zosh.service;

import com.zosh.payload.dto.UserDto;
import com.zosh.payload.responce.AuthResponse;

public interface AuthService {
    AuthResponse signup(UserDto userDto);
    AuthResponse login(UserDto userDto);
}
