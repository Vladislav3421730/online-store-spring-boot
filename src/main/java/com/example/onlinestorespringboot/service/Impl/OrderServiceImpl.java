package com.example.onlinestorespringboot.service.Impl;

import com.example.onlinestorespringboot.dto.OrderDto;
import com.example.onlinestorespringboot.exception.OrderNotFoundException;
import com.example.onlinestorespringboot.mapper.OrderMapper;
import com.example.onlinestorespringboot.model.Order;
import com.example.onlinestorespringboot.repository.OrderRepository;
import com.example.onlinestorespringboot.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;
    OrderMapper orderMapper;

    @Override
    public Page<OrderDto> findAll(PageRequest pageRequest) {
        log.info("Fetching all orders");
        return orderRepository.findAll(pageRequest)
                .map(orderMapper::toDTO);
    }

    @Override
    public OrderDto findById(Long id) {
        log.info("Fetching order by ID: {}", id);
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new OrderNotFoundException("Order with id " + id + " not found"));
        log.info("Found order with ID: {}", id);
        return orderMapper.toDTO(order);
    }

    @Override
    public Page<OrderDto> findAllByUserEmail(String email, PageRequest pageRequest) {
        log.info("Fetching orders for user with email: {}", email);
        return orderRepository.findAllByUserEmail(email, pageRequest)
                .map(orderMapper::toDTO);
    }

    @Override
    public Page<OrderDto> findAllByUserId(Long id, Pageable pageRequest) {
        log.info("Fetching all by user id");
        return orderRepository.findAllByUserId(id, pageRequest)
                .map(orderMapper::toDTO);
    }

    @Override
    @Transactional
    public void update(OrderDto orderDto) {
        log.info("Updating order with ID: {}", orderDto.getId());
        if (orderRepository.existsById(orderDto.getId())) {
            Order order = orderMapper.toEntity(orderDto);
            orderRepository.save(order);
            log.info("Order with ID: {} updated successfully", orderDto.getId());
        } else {
            log.info("Order with id {} not found", orderDto.getId());
            throw new OrderNotFoundException("Order with id " + orderDto.getId() + " not found");
        }
    }
}
