package com.example.onlinestorespringboot.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDto {

    @NotBlank(message = "Title is required")
    @Size(min = 3, message = "Title's size must be more or equal than 3")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, message = "Description's size must be more or equal than 10")
    private String description;

    @NotBlank(message = "Category is required")
    @Size(min = 3, message = "Category's size must be more or equal than 3")
    private String category;

    @Min(value = 0, message = "Amount must be more or equal than 0")
    @NotNull(message = "Amount is required")
    private Integer amount;

    @DecimalMin(value = "0.01", message = "Cost must be greater than or equal to 0.01")
    @NotNull(message = "Coast is required")
    private BigDecimal coast;
}

