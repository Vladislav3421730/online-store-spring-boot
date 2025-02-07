package com.example.onlinestorespringboot.service;

import com.example.onlinestorespringboot.dto.*;

public interface AuthService {

    JwtResponseDto createAuthToken(LoginUserDto user);

    JwtResponseDto refreshToken(String token);

    UserDto registerUser(RegisterUserDto user);


}
