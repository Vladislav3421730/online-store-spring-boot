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
    I18nUtil i18nUtil;

    @PostMapping
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
    public ResponseEntity<ResponseDto> incrementAmountOfProduct(@PathVariable Integer index) {
        UserDto user = userService.getUser();
        if (!cartService.incrementAmountOfCartInBasket(user.getCarts(), index)) {
            throw new ExceedingQuantityException(i18nUtil.getMessage(Messages.CART_ERROR_EXCEEDING_QUANTITY));
        }
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.CART_SUCCESS_CART_UPDATED)));
    }

    @PutMapping("/decrement/{index}")
    public ResponseEntity<ResponseDto> decrementAmountOfProduct(@PathVariable Integer index) {
        UserDto user = userService.getUser();
        cartService.decrementAmountOfCartInBasket(user.getCarts(), index);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.CART_SUCCESS_CART_UPDATED)));
    }

    @DeleteMapping("/delete/{index}")
    public ResponseEntity<ResponseDto> deleteProductFromCart(@PathVariable Integer index) {
        UserDto user = userService.getUser();
        cartService.deleteCartFromBasket(user.getCarts(), index);
        return ResponseEntity.ok(new ResponseDto(i18nUtil.getMessage(Messages.CART_SUCCESS_CART_DELETED)));
    }
}
