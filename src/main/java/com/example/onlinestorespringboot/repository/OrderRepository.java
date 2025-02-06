package com.example.onlinestorespringboot.repository;


import com.example.onlinestorespringboot.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserEmail(String email, Pageable pageable);
    Page<Order> findAllByUserId(Long id, Pageable pageable);
}
