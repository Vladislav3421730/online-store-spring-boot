package com.example.onlinestorespringboot.service.Impl;

import com.example.onlinestorespringboot.dto.CartDto;
import com.example.onlinestorespringboot.exception.WrongIndexException;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.mapper.CartMapper;
import com.example.onlinestorespringboot.model.Cart;
import com.example.onlinestorespringboot.repository.CartRepository;
import com.example.onlinestorespringboot.service.CartService;
import com.example.onlinestorespringboot.util.Messages;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CartServiceImpl implements CartService {

    CartRepository cartRepository;
    CartMapper cartMapper;
    I18nUtil i18nUtil;

    @Override
    @Transactional
    public boolean incrementAmountOfCartInBasket(List<CartDto> userCarts, int index) {
        if (index < 0 || index >= userCarts.size()) {
            log.error("Index out of bounds: {}", index);
            throw new WrongIndexException(i18nUtil.getMessage(Messages.CART_ERROR_INDEX_OUT_OF_BOUNDS, String.valueOf(index)));
        }
        CartDto cart = userCarts.get(index);
        if (cart.getAmount() + 1 > cart.getProduct().getAmount()) {
            log.error("The quantity of goods in the basket is equal to the quantity in the warehouse, " +
                            "it is impossible to increase the quantity of goods {}. Available quantity in warehouse: {}",
                    cart.getProduct().getTitle(), cart.getProduct().getAmount());
            throw new IllegalStateException(i18nUtil.getMessage(Messages.CART_ERROR_QUANTITY_EXCEEDS_STOCK,
                    cart.getProduct().getTitle(), String.valueOf(cart.getProduct().getAmount())));
        }
        cart.setAmount(cart.getAmount() + 1);
        userCarts.set(index, cart);
        Cart updatedCart = cartMapper.toEntity(cart);
        cartRepository.save(updatedCart);
        log.info("The quantity of product '{}' has been increased by 1. New quantity: {}",
                cart.getProduct().getTitle(), cart.getAmount());
        return true;
    }

    @Override
    @Transactional
    public void decrementAmountOfCartInBasket(List<CartDto> userCarts, int index) {
        if (index < 0 || index >= userCarts.size()) {
            log.error("Index out of bounds: {}", index);
            throw new WrongIndexException(i18nUtil.getMessage(Messages.CART_ERROR_INDEX_OUT_OF_BOUNDS, String.valueOf(index)));
        }
        CartDto cart = userCarts.get(index);
        if (cart.getAmount() == 1) {
            userCarts.remove(index);
            cartRepository.deleteById(cart.getId());
            log.info("The product '{}' has been removed from the user's cart", cart.getProduct().getTitle());
        } else {
            cart.setAmount(cart.getAmount() - 1);
            Cart updatedCart = cartMapper.toEntity(cart);
            cartRepository.save(updatedCart);
            log.info("The quantity of product '{}' has been reduced by 1. New quantity: {}",
                    cart.getProduct().getTitle(), cart.getAmount());
        }
    }

    @Override
    @Transactional
    public void deleteCartFromBasket(List<CartDto> cartAfterRemoving, int index) {
        if (index < 0 || index >= cartAfterRemoving.size()) {
            log.error("Index out of bounds: {}", index);
            throw new WrongIndexException(i18nUtil.getMessage(Messages.CART_ERROR_INDEX_OUT_OF_BOUNDS, String.valueOf(index)));
        }
        cartRepository.deleteById(cartAfterRemoving.get(index).getId());
    }


}
