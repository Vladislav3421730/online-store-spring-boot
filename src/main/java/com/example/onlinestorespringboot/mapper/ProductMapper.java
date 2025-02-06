package com.example.onlinestorespringboot.mapper;

import com.example.onlinestorespringboot.dto.CreateProductDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.model.Product;
import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {

    Product toNewEntity(CreateProductDto dto);

    ProductDto toDTO(Product product);

    Product toEntity(ProductDto productDto);

}