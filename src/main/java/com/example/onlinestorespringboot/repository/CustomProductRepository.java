package com.example.onlinestorespringboot.repository;


import com.example.onlinestorespringboot.dto.ProductFilterDTO;
import com.example.onlinestorespringboot.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


public interface CustomProductRepository {
    Page<Product> findAllByFilter(ProductFilterDTO productFilterDTO, PageRequest pageRequest);
    void deleteProductWithOrderItems(Long productId);
}
