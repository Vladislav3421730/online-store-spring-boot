package com.example.factory;

import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.model.Product;

import java.math.BigDecimal;

public class ProductFactory {
    public static Product createValidProduct() {
        return Product.builder()
                .title("Valid Title")
                .description("Valid description with enough length")
                .category("Valid Category")
                .amount(10)
                .coast(new BigDecimal("10.50"))
                .build();
    }

    public static Product createInvalidProduct() {
        return Product.builder()
                .title("Valid Title")
                .amount(10)
                .coast(new BigDecimal("10.50"))
                .build();
    }

    public static ProductDto createProductDto(Long id, String title) {
        return ProductDto.builder()
                .id(id)
                .title(title)
                .build();
    }

    public static Product createProduct(Long id, String title) {
        return Product.builder()
                .id(id)
                .title(title)
                .build();
    }
}
