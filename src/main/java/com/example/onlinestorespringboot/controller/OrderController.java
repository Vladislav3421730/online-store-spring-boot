package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.OrderDto;
import com.example.onlinestorespringboot.dto.ResponseDto;
import com.example.onlinestorespringboot.service.OrderService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OrderController {

    OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDto>> findAllOrders(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy) {
        if (offset == null) offset = 0;
        if (pageSize == null) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "createdAt";
        Page<OrderDto> orders = orderService.findAll(PageRequest.of(offset, pageSize, Sort.by(sortBy)));
        return ResponseEntity.ok(orders);
    }

    @PutMapping
    public ResponseEntity<ResponseDto> updateOrder(@RequestBody @Valid OrderDto orderDto) {
        orderService.update(orderDto);
        return ResponseEntity.ok(new ResponseDto("Order was updated successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> findOrderById(@PathVariable Long id) {
        OrderDto order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/email")
    public ResponseEntity<Page<OrderDto>> findOrdersByUserEmail(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(name = "email") String email) {
        if (offset == null) offset = 0;
        if (pageSize == null) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "createdAt";
        Page<OrderDto> orders = orderService.findAllByUserEmail(email, PageRequest.of(offset, pageSize, Sort.by(sortBy)));
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/user/id/{id}")
    public ResponseEntity<Page<OrderDto>> findOrdersByUserId(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @PathVariable Long id) {
        if (offset == null) offset = 0;
        if (pageSize == null) pageSize = 10;
        if (sortBy == null || sortBy.isEmpty()) sortBy = "createdAt";
        Page<OrderDto> orders = orderService.findAllByUserId(id, PageRequest.of(offset, pageSize, Sort.by(sortBy)));
        return ResponseEntity.ok(orders);
    }

}
