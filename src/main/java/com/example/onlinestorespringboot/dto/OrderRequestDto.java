package com.example.onlinestorespringboot.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDto {
    @NotNull(message = "Address must be not null")
    private AddressDto address;

    @NotNull(message = "Total coast must be not null")
    private Double totalCoast;
}

