package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.*;
import com.example.onlinestorespringboot.service.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthController {

    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> createToken(@RequestBody @Valid LoginUserDto userDto) {
        JwtResponseDto jwtResponseDto = authService.createAuthToken(userDto);
        return ResponseEntity.ok(jwtResponseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid RegisterUserDto registerUserDto) {
        UserDto userDto = authService.registerUser(registerUserDto);
        return ResponseEntity.ok(userDto);
    }
}
