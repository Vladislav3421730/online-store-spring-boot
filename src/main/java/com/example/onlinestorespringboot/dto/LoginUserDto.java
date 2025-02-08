package com.example.onlinestorespringboot.dto;



import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO for user login containing email and password")
public class LoginUserDto {

    @NotNull(message = "Email must be not null")
    @Email(message = "Email must contains @")
    @Schema(description = "User's email address", example = "user@example.com")
    private String email;

    @NotNull(message = "Password must be not null")
    @Schema(description = "User's password", example = "password123")
    private String password;
}
