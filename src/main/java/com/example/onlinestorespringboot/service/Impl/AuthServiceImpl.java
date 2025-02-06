package com.example.onlinestorespringboot.service.Impl;

import com.example.onlinestorespringboot.dto.*;
import com.example.onlinestorespringboot.exception.LoginFailedException;
import com.example.onlinestorespringboot.exception.RegistrationFailedException;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.mapper.UserMapper;
import com.example.onlinestorespringboot.model.User;
import com.example.onlinestorespringboot.model.enums.Role;
import com.example.onlinestorespringboot.repository.UserRepository;
import com.example.onlinestorespringboot.service.AuthService;
import com.example.onlinestorespringboot.util.JwtTokenUtils;
import com.example.onlinestorespringboot.util.Messages;
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
    I18nUtil i18nUtil;

    @Override
    public JwtResponseDto createAuthToken(LoginUserDto user) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            log.error("error: {}", badCredentialsException.getMessage());
            throw new LoginFailedException(i18nUtil.getMessage(Messages.LOGIN_ERROR,user.getEmail()));
        }
        User userDB = userRepository.findByEmail(user.getEmail()).get();
        return new JwtResponseDto(jwtTokenUtils.generateToken(userDB));
    }

    @Override
    public UserDto registerUser(RegisterUserDto user) {
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new RegistrationFailedException(i18nUtil.getMessage(Messages.REGISTRATION_ERROR_PASSWORD_MISMATCH));
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RegistrationFailedException(i18nUtil.getMessage(Messages.REGISTRATION_ERROR_EMAIL_EXISTS, user.getEmail()));
        }
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new RegistrationFailedException(i18nUtil.getMessage(Messages.REGISTRATION_ERROR_PHONE_EXISTS, user.getEmail()));
        }
        User userDB = userMapper.toNewEntity(user);
        userDB.setPassword(passwordEncoder.encode(userDB.getPassword()));
        userDB.setRoleSet(Set.of(Role.ROLE_USER));
        User savedUser = userRepository.save(userDB);
        return userMapper.toDTO(savedUser);
    }
}
