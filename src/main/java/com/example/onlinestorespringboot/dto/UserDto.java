package com.example.onlinestorespringboot.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    @Size(min = 5, message = "Username length must be more or equal than 5")
    private String username;
    @Size(min = 6, message = "Password length must be more or equal than 6 ")
    private String password;
    @Email(message = "Email must contains @")
    private String email;
    @NotNull
    private boolean bun;
    @Column(name = "phone_number", unique = true)
    @Pattern(regexp = "^[+]375[0-9]{9}$", message = "Phone number must be in format +375XXXXXXXXX")
    private String phoneNumber;
    private Set<String> roleSet;
    private List<CartDto> carts;
    private List<OrderDto> orders;


}
