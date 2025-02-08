package com.example.factory;

import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.dto.RegisterUserDto;
import com.example.onlinestorespringboot.model.Product;
import com.example.onlinestorespringboot.model.User;

public class UserFactory {

    public static User createUser() {
        return User.builder()
                .username("user")
                .password("password")
                .email("vlados@gmail.com")
                .phoneNumber("+375445713490")
                .build();
    }


    public static User createUpdatedUser() {
        return User.builder()
                .id(1L)
                .password("password")
                .username("vlados")
                .email("other@gmail.com")
                .phoneNumber("+375445713456")
                .build();
    }

    public static User createUserWithInvalidData() {
        return User.builder()
                .username("vlados")
                .phoneNumber("+375445713490")
                .build();
    }

    public static RegisterUserDto createRegisterUserDto() {
        return RegisterUserDto.builder()
                .username("vlad")
                .email("vlad@gmail.com")
                .password("q1w2e3")
                .confirmPassword("q1w2e3")
                .phoneNumber("+375445672387")
                .build();
    }


}
