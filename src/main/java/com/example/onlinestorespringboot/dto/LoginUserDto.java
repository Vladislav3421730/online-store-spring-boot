package com.example.onlinestorespringboot.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginUserDto {

    @NotNull(message = "Email must be not null")
    @Email(message = "Email must contains @")
    private String email;

    @NotNull(message = "Password must be not null")
    private String password;
}
