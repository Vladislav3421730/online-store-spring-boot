package com.example.onlinestorespringboot.controller;

import com.example.onlinestorespringboot.dto.*;
import com.example.onlinestorespringboot.exception.ExceedingQuantityException;
import com.example.onlinestorespringboot.exception.NoQuantityProductException;
import com.example.onlinestorespringboot.exception.PaymentFailedException;
import com.example.onlinestorespringboot.service.CartService;
import com.example.onlinestorespringboot.service.ProductService;
import com.example.onlinestorespringboot.service.UserService;
import com.example.onlinestorespringboot.util.OrderPayingValidator;
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
public class CartController {

    ProductService productService;
    UserService userService;
    CartService cartService;

    @PostMapping
    public ResponseEntity<ResponseDto> makeOrder(@RequestBody @Valid OrderRequestDto orderRequest) {
        log.info("Received order request: {}", orderRequest);
        UserDto user = userService.getUser();
        BigDecimal totalPrice = BigDecimal.valueOf(orderRequest.getTotalCoast());
        if (!OrderPayingValidator.validateOrderCoast(totalPrice)) {
            throw new PaymentFailedException("Payment was failed");
        }
        userService.makeOrder(user, orderRequest);
        return ResponseEntity.ok(new ResponseDto("Order was created successfully"));
    }

    @PostMapping("/add/{id}")
    public ResponseEntity<ResponseDto> addProductToCart(@PathVariable Long id) {
        ProductDto productDto = productService.findById(id);
        if (productDto.getAmount() == 0) {
            throw new NoQuantityProductException(String.format("Amount of product %s is 0. You can't add that to cart", productDto.getTitle()));
        }
        UserDto userDto = userService.getUser();
        userService.addProductToCart(userDto, productDto);
        return ResponseEntity.ok(new ResponseDto(String.format("Product %s was added successfully to user %s cart", productDto.getTitle(), userDto.getEmail())));
    }

    @PutMapping("/increment/{index}")
    public ResponseEntity<ResponseDto> incrementAmountOfProduct(@PathVariable Integer index) {
        UserDto user = userService.getUser();
        if (!cartService.incrementAmountOfCartInBasket(user.getCarts(), index)) {
            throw new ExceedingQuantityException("The quantity of goods in the cart cannot exceed the quantity in stock");
        }
        return ResponseEntity.ok(new ResponseDto("Cart was updated successfully"));
    }

    @PutMapping("/decrement/{index}")
    public ResponseEntity<ResponseDto> decrementAmountOfProduct(@PathVariable Integer index) {
        UserDto user = userService.getUser();
        cartService.decrementAmountOfCartInBasket(user.getCarts(), index);
        return ResponseEntity.ok(new ResponseDto("Cart was updated successfully"));
    }

    @DeleteMapping("/delete/{index}")
    public ResponseEntity<ResponseDto> deleteProductFromCart(@PathVariable Integer index) {
        UserDto user = userService.getUser();
        cartService.deleteCartFromBasket(user.getCarts(),index);
        return ResponseEntity.ok(new ResponseDto("Cart was deleted successfully"));
    }


}
