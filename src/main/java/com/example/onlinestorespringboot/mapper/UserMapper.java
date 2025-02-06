package com.example.onlinestorespringboot.mapper;

import com.example.onlinestorespringboot.dto.CartDto;
import com.example.onlinestorespringboot.dto.OrderDto;
import com.example.onlinestorespringboot.dto.RegisterUserDto;
import com.example.onlinestorespringboot.dto.UserDto;
import com.example.onlinestorespringboot.model.Cart;
import com.example.onlinestorespringboot.model.Order;
import com.example.onlinestorespringboot.model.User;
import com.example.onlinestorespringboot.model.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper
public interface UserMapper {

    OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);
    CartMapper cartMapper = Mappers.getMapper(CartMapper.class);

    User toNewEntity(RegisterUserDto registerUserDto);

    UserDto toDTO(User user);

    User toEntity(UserDto userDto);

    default List<CartDto> mapCartsToCartDtos(List<Cart> carts) {
        if (carts == null) {
            return null;
        }
        return carts.stream()
                .map(cartMapper::toDTO)
                .collect(Collectors.toList());
    }

    default List<OrderDto> mapOrdersToOrderDtos(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    default List<Cart> mapCartsDtosToCart(List<CartDto> carts) {
        if (carts == null) {
            return null;
        }
        return carts.stream()
                .map(cartMapper::toEntity)
                .collect(Collectors.toList());
    }

    default List<Order> mapOrdersDtosToOrders(List<OrderDto> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream()
                .map(orderMapper::toEntity)
                .collect(Collectors.toList());
    }

    default Set<String> mapImageListToIds(Set<Role> roleSet) {
        if (roleSet == null) {
            return null;
        }
        return roleSet.stream()
                .map(Role::name)
                .collect(Collectors.toSet());
    }

}

