package com.example.onlinestorespringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {

    private Long id;
    private int amount;
    private ProductDto product;
    private Long userId;
}
