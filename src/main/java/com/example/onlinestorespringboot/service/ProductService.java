package com.example.onlinestorespringboot.service;

import com.example.onlinestorespringboot.dto.CreateProductDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.dto.ProductFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    void save(CreateProductDto createProductDTO, List<MultipartFile> files);

    Page<ProductDto> findAll(PageRequest pageRequest);

    ProductDto findById(Long id);

    Page<ProductDto> findAllByTitle(String title, PageRequest pageRequest);

    Page<ProductDto> findAllByFilter(ProductFilterDTO productFilterDTO, PageRequest pageRequest);

    ProductDto update(ProductDto product);

    void delete(Long id);

}
