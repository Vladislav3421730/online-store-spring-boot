package com.example.onlinestorespringboot.service.Impl;


import com.example.onlinestorespringboot.dto.*;
import com.example.onlinestorespringboot.exception.EmptyCartException;
import com.example.onlinestorespringboot.exception.UserNotFoundException;
import com.example.onlinestorespringboot.i18n.I18nUtil;
import com.example.onlinestorespringboot.mapper.ProductMapper;
import com.example.onlinestorespringboot.mapper.UserMapper;
import com.example.onlinestorespringboot.model.Cart;
import com.example.onlinestorespringboot.model.Order;
import com.example.onlinestorespringboot.model.User;
import com.example.onlinestorespringboot.model.enums.Role;
import com.example.onlinestorespringboot.repository.UserRepository;
import com.example.onlinestorespringboot.service.UserService;
import com.example.onlinestorespringboot.util.MakeOrderUtils;
import com.example.onlinestorespringboot.util.Messages;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    ProductMapper productMapper;

    PasswordEncoder passwordEncoder;

    I18nUtil i18nUtil;
    MakeOrderUtils makeOrderUtils;

    @Override
    public UserDto findByEmail(String email) {
        log.info("Trying find user by email {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException(i18nUtil.getMessage(Messages.USER_ERROR_EMAIL_NOT_FOUND, email)));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UserDto saveUser(RegisterUserDto registerUserDto) {
        log.info("Creating new user entity for: {}", registerUserDto.getEmail());
        User userDB = userMapper.toNewEntity(registerUserDto);
        userDB.setPassword(passwordEncoder.encode(userDB.getPassword()));
        userDB.setRoleSet(Set.of(Role.ROLE_USER));

        User savedUser = userRepository.save(userDB);
        log.info("User {} registered successfully", userDB.getEmail());
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDto getUser() {
        log.info("Trying get user by authentication in security");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = (String) authentication.getPrincipal();
            return findByEmail(email);
        }
        throw new UserNotFoundException(i18nUtil.getMessage(Messages.USER_ERROR_CONTEXT_NOT_FOUND));
    }

    @Override
    public UserDto findById(Long id) {
        log.info("Trying find user by id {}", id);
        User user = userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(i18nUtil.getMessage(Messages.USER_ERROR_ID_NOT_FOUND, String.valueOf(id))));
        return userMapper.toDTO(user);
    }

    @Override
    public Page<UserDto> findAll(PageRequest pageRequest) {
        log.info("Find all users");
        return userRepository.findAll(pageRequest)
                .map(userMapper::toDTO);
    }

    @Override
    @Transactional
    public void addProductToCart(UserDto userDto, ProductDto productDto) {
        log.info("Adding product to cart: {} for user: {}", productDto.getTitle(), userDto.getEmail());

        User user = userMapper.toEntity(userDto);
        boolean isInCartList = user.getCarts().stream()
                .filter(cart -> cart.getProduct().getId().equals(productDto.getId()))
                .peek(cart -> cart.setAmount(cart.getAmount() + 1))
                .findFirst()
                .isPresent();

        if (!isInCartList) {
            log.info("Adding a new item {} to the user's cart", productDto.getTitle());
            Cart cart = new Cart(productMapper.toEntity(productDto));
            user.addCartToList(cart);
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void makeOrder(UserDto userDto, OrderRequestDto orderRequestDto) {
        log.info("Making an order for user: {}", userDto.getEmail());

        User user = userMapper.toEntity(userDto);
        if (user.getCarts().isEmpty()) {
            throw new EmptyCartException(i18nUtil.getMessage(Messages.CART_ERROR_EMPTY_CART));
        }
        Order order = makeOrderUtils.createOrder(orderRequestDto);
        makeOrderUtils.processAddress(user, order);
        makeOrderUtils.createOrderItems(user, order);

        makeOrderUtils.finalizeOrder(user, order);
        log.info("Order for user {} created successfully", userDto.getEmail());
    }


}
