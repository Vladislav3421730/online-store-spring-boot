package com.example.onlinestorespringboot.service.Impl;

import com.example.onlinestorespringboot.dto.*;
import com.example.onlinestorespringboot.exception.LoginFailedException;
import com.example.onlinestorespringboot.exception.RegistrationFailedException;
import com.example.onlinestorespringboot.mapper.UserMapper;
import com.example.onlinestorespringboot.model.User;
import com.example.onlinestorespringboot.model.enums.Role;
import com.example.onlinestorespringboot.repository.UserRepository;
import com.example.onlinestorespringboot.service.AuthService;
import com.example.onlinestorespringboot.util.JwtTokenUtils;
import com.example.onlinestorespringboot.wrapper.UserDetailsWrapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    AuthenticationManager authenticationManager;
    JwtTokenUtils jwtTokenUtils;


    @Override
    public JwtResponseDto createAuthToken(LoginUserDto user) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            throw new LoginFailedException(String.format("Invalid username or password for user %s", user.getEmail()));
        }
        User userDB = userRepository.findByEmail(user.getEmail()).get();
        return new JwtResponseDto(jwtTokenUtils.generateToken(userDB));
    }

    @Override
    public UserDto registerUser(RegisterUserDto user) {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new RegistrationFailedException("Passwords should be the same");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RegistrationFailedException(String.format("User with email %s already exist is system", user.getEmail()));
        }
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new RegistrationFailedException(String.format("User with phone %s already exist is system", user.getPhoneNumber()));
        }
        User userDB = userMapper.toNewEntity(user);
        userDB.setPassword(passwordEncoder.encode(userDB.getPassword()));
        userDB.setRoleSet(Set.of(Role.ROLE_USER));
        User savedUser = userRepository.save(userDB);
        return userMapper.toDTO(savedUser);
    }
}
