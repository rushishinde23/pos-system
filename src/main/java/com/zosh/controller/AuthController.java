package com.zosh.controller;

import com.zosh.exceptions.UserException;
import com.zosh.payload.dto.UserDto;
import com.zosh.payload.responce.AuthResponse;
import com.zosh.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

   private final AuthService authService;


   // http://localhost 5000/auth/signup

   @PostMapping("/signup")
   public ResponseEntity<AuthResponse> signupHandler(
           @RequestBody UserDto userDto) throws UserException {
        return ResponseEntity.ok(authService.signup(userDto));
   }
   @PostMapping("/login")
   public ResponseEntity<AuthResponse> loginHandler(
           @RequestBody UserDto userDto) throws UserException {
        return ResponseEntity.ok(authService.login(userDto));
   }
}
