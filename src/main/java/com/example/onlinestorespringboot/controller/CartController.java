package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.*;
import com.example.onlinestorespringboot.exception.ExceedingQuantityException;
import com.example.onlinestorespringboot.exception.NoQuantityProductException;
import com.example.onlinestorespringboot.exception.PaymentFailedException;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.service.CartService;
import com.example.onlinestorespringboot.service.ProductService;
import com.example.onlinestorespringboot.service.UserService;
import com.example.onlinestorespringboot.util.Messages;
import com.example.onlinestorespringboot.util.OrderPayingValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/cart")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "Cart", description = "Endpoints for managing cart items (add, remove, update quantity, etc.)")
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
public class CartController {

    ProductService productService;
    UserService userService;
    CartService cartService;
    I18nUtil i18nUtil;

    @PostMapping
    @Operation(summary = "Create an order", description = "Processes the order request and creates an order for the user.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error with making order",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<ResponseDto> makeOrder(@RequestBody @Valid OrderRequestDto orderRequest) {
        log.info("Received order request: {}", orderRequest);
        UserDto user = userService.getUser();
        BigDecimal totalPrice = BigDecimal.valueOf(orderRequest.getTotalCoast());
        if (!OrderPayingValidator.validateOrderCoast(totalPrice)) {
            throw new PaymentFailedException(i18nUtil.getMessage(Messages.CART_ERROR_EXCEEDING_QUANTITY));
        }
        userService.makeOrder(user, orderRequest);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.CART_SUCCESS_ORDER_CREATED)));
    }

    @PostMapping("/add/{id}")
    @Operation(summary = "Add product to cart", description = "Adds a product to the user's cart by product ID.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product added successfully to cart",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error during adding product",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<ResponseDto> addProductToCart(@PathVariable Long id) {
        ProductDto productDto = productService.findById(id);
        if (productDto.getAmount() == 0) {
            throw new NoQuantityProductException(i18nUtil.getMessage(Messages.CART_ERROR_NO_QUANTITY_PRODUCT, productDto.getTitle()));
        }
        UserDto userDto = userService.getUser();
        userService.addProductToCart(userDto, productDto);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.CART_SUCCESS_PRODUCT_ADDED, productDto.getTitle(), userDto.getEmail())));
    }

    @PutMapping("/increment/{index}")
    @Operation(summary = "Increment product quantity", description = "Increases the quantity of a product in the user's cart by index.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product in cart was incremented successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error during incrementing product in cart",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<ResponseDto> incrementAmountOfProduct(@PathVariable Integer index) {
        UserDto user = userService.getUser();
        if (!cartService.incrementAmountOfCartInBasket(user.getCarts(), index)) {
            throw new ExceedingQuantityException(i18nUtil.getMessage(Messages.CART_ERROR_EXCEEDING_QUANTITY));
        }
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.CART_SUCCESS_CART_UPDATED)));
    }

    @PutMapping("/decrement/{index}")
    @Operation(summary = "Decrement product quantity", description = "Decreases the quantity of a product in the user's cart by index.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product in cart was decremented successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error during decrementing product in cart",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<ResponseDto> decrementAmountOfProduct(@PathVariable Integer index) {
        UserDto user = userService.getUser();
        cartService.decrementAmountOfCartInBasket(user.getCarts(), index);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.CART_SUCCESS_CART_UPDATED)));
    }


    @DeleteMapping("/delete/{index}")
    @Operation(summary = "Delete product from cart", description = "Removes a product from the user's cart by index.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product in cart was deleted successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error during deleting product in cart",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AppErrorDto.class))
            )
    })
    public ResponseEntity<ResponseDto> deleteProductFromCart(@PathVariable Integer index) {
        UserDto user = userService.getUser();
        cartService.deleteCartFromBasket(user.getCarts(), index);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.CART_SUCCESS_CART_DELETED)));
    }
}
