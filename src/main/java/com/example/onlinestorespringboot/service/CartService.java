package com.example.onlinestorespringboot.service;


import com.example.onlinestorespringboot.dto.CartDto;

import java.util.List;

public interface CartService {

    boolean incrementAmountOfCartInBasket(List<CartDto> userCarts, int index);

    void decrementAmountOfCartInBasket(List<CartDto> userCarts, int index);

    void deleteCartFromBasket(List<CartDto> cartAfterRemoving, int index);
}
