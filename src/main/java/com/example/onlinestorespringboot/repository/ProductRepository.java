package com.example.onlinestorespringboot.repository;


import com.example.onlinestorespringboot.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {

    Page<Product> findAllByTitleContainingIgnoreCase(String title, PageRequest pageRequest);
}
