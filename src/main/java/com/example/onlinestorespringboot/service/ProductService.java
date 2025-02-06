package com.example.onlinestorespringboot.service;

import com.example.onlinestorespringboot.dto.CreateProductDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.dto.ProductFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductService {

    void save(CreateProductDto createProductDTO);

    Page<ProductDto> findAll(PageRequest pageRequest);

    ProductDto findById(Long id);

    Page<ProductDto> findAllByTitle(String title, PageRequest pageRequest);

    Page<ProductDto> findAllByFilter(ProductFilterDTO productFilterDTO, PageRequest pageRequest);

    ProductDto update(ProductDto product);

    void delete(Long id);

}
