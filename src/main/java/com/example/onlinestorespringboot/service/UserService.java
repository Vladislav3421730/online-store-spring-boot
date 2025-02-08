package com.example.onlinestorespringboot.service;


import com.example.onlinestorespringboot.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserService {

    UserDto saveUser(RegisterUserDto registerUserDto);

    UserDto getUser();

    UserDto findByEmail(String email);

    UserDto findById(Long id);

    Page<UserDto> findAll(PageRequest pageRequest);

    void addProductToCart(UserDto user, ProductDto product);

    void makeOrder(UserDto userDto, OrderRequestDto orderRequestDto);
}
