package com.zosh.service;

import com.zosh.exceptions.UserException;
import com.zosh.payload.dto.UserDto;
import com.zosh.payload.responce.AuthResponse;

public interface AuthService {
    AuthResponse signup(UserDto userDto) throws UserException;
    AuthResponse login(UserDto userDto) throws UserException;
}
