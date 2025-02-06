package com.example.onlinestorespringboot.dto;

import com.example.onlinestorespringboot.annotation.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private LocalDateTime createdAt;
    @DecimalMin(value = "0.01", message = "total price must be more or equal than 0.01")
    private BigDecimal totalPrice;
    private AddressDto address;
    @NotNull(message = "User must be not null")
    private Long userId;
    private String email;
    @OrderStatus
    private String status;
    private List<OrderItemDto> orderItems;

}
