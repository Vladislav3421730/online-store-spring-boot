package com.example.onlinestorespringboot.service.Impl;

import com.example.onlinestorespringboot.dto.*;
import com.example.onlinestorespringboot.exception.LoginFailedException;
import com.example.onlinestorespringboot.exception.RefreshTokenException;
import com.example.onlinestorespringboot.exception.RegistrationFailedException;
import com.example.onlinestorespringboot.exception.UserNotFoundException;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.model.User;
import com.example.onlinestorespringboot.repository.UserRepository;
import com.example.onlinestorespringboot.service.AuthService;
import com.example.onlinestorespringboot.service.UserService;
import com.example.onlinestorespringboot.util.JwtAccessTokenUtils;
import com.example.onlinestorespringboot.util.JwtRefreshTokenUtils;
import com.example.onlinestorespringboot.util.Messages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthServiceImpl implements AuthService {

    UserRepository userRepository;
    UserService userService;
    AuthenticationManager authenticationManager;
    JwtAccessTokenUtils jwtAccessTokenUtils;
    JwtRefreshTokenUtils jwtRefreshTokenUtils;
    I18nUtil i18nUtil;

    @Override
    public JwtResponseDto createAuthToken(LoginUserDto user) {
        try {
            log.info("Attempting authentication for user: {}", user.getEmail());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        } catch (BadCredentialsException badCredentialsException) {
            log.error("error: {}", badCredentialsException.getMessage());
            throw new LoginFailedException(i18nUtil.getMessage(Messages.LOGIN_ERROR, user.getEmail()));
        }

        User userDB = userRepository.findByEmail(user.getEmail()).orElseThrow(() ->
                new UserNotFoundException(i18nUtil.getMessage(Messages.USER_ERROR_EMAIL_NOT_FOUND, user.getEmail())));

        log.info("User {} authenticated successfully", user.getEmail());
        return JwtResponseDto.builder()
                .accessToken(jwtAccessTokenUtils.generateAccessToken(userDB))
                .refreshToken(jwtRefreshTokenUtils.generateRefreshToken(user.getEmail()))
                .build();
    }

    @Override
    public JwtResponseDto refreshToken(String token) {
        log.info("Processing refresh token");
        if (!jwtRefreshTokenUtils.isRefreshTokenValid(token)) {
            log.error("Refresh token validation failed: token is expired or invalid");
            throw new RefreshTokenException(i18nUtil.getMessage(Messages.REFRESH_TOKEN_ERROR));
        }
        String email = jwtRefreshTokenUtils.getEmailFromRefreshToken(token);
        log.info("Refresh token is valid. Extracted email: {}", email);
        User userDB = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException(i18nUtil.getMessage(Messages.USER_ERROR_EMAIL_NOT_FOUND, email)));

        log.info("Issuing new access token for user: {}", email);
        return JwtResponseDto.builder()
                .accessToken(jwtAccessTokenUtils.generateAccessToken(userDB))
                .refreshToken(token)
                .build();
    }

    @Override
    public UserDto registerUser(RegisterUserDto user) {
        log.info("Starting registration process for user: {}", user.getEmail());

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            log.error("Password mismatch for user: {}", user.getEmail());
            throw new RegistrationFailedException(i18nUtil.getMessage(Messages.REGISTRATION_ERROR_PASSWORD_MISMATCH));
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.error("Email {} is already in use", user.getEmail());
            throw new RegistrationFailedException(i18nUtil.getMessage(Messages.REGISTRATION_ERROR_EMAIL_EXISTS, user.getEmail()));
        }

        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            log.error("Phone number {} is already in use", user.getPhoneNumber());
            throw new RegistrationFailedException(i18nUtil.getMessage(Messages.REGISTRATION_ERROR_PHONE_EXISTS, user.getEmail()));
        }
        return userService.saveUser(user);

    }
}
