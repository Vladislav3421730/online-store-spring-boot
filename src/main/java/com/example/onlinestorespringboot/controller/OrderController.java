package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.AppErrorDto;
import com.example.onlinestorespringboot.dto.OrderDto;
import com.example.onlinestorespringboot.dto.ResponseDto;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.service.OrderService;
import com.example.onlinestorespringboot.util.Messages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Order",description = "Endpoints for managing orders")
@ApiResponses({
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
        ),
        @ApiResponse(
                responseCode = "403",
                description = "Forbidden",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
        )
})
public class OrderController {

    OrderService orderService;
    I18nUtil i18nUtil;

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Find all orders", description = "Retrieves a paginated list of all orders.")
    @ApiResponse(
            responseCode = "200",
            description = "Find All Orders (pagination included)",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = OrderDto.class)))
    )
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
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update an order", description = "Updates an existing order based on the provided order details.")
    @ApiResponse(
            responseCode = "200",
            description = "Order was updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
    )
    public ResponseEntity<ResponseDto> updateOrder(@RequestBody @Valid OrderDto orderDto) {
        orderService.update(orderDto);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.ORDER_SUCCESS_UPDATED)));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Find order by ID", description = "Retrieves an order by its unique identifier.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order was found successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Order not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<OrderDto> findOrderById(@PathVariable Long id) {
        OrderDto order = orderService.findById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/email")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Find all orders by user email", description = "Retrieves a paginated list of all orders associated with a specific user email.")
    @ApiResponse(
            responseCode = "200",
            description = "Find All Orders by user email (pagination included)",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = OrderDto.class)))
    )
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
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Find all orders by user ID", description = "Retrieves a paginated list of all orders associated with a specific user ID.")
    @ApiResponse(
            responseCode = "200",
            description = "Find All Orders by user id (pagination included)",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = OrderDto.class)))
    )
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
