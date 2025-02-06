package com.example.onlinestorespringboot.config;

import com.example.onlinestorespringboot.mapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public UserMapper userMapper() {
        return new UserMapperImpl();
    }

    @Bean
    public ProductMapper productMapper() {
        return new ProductMapperImpl();
    }

    @Bean
    public OrderMapper orderMapper() {
        return new OrderMapperImpl();
    }

    @Bean
    public CartMapper cartMapper() {
        return new CartMapperImpl();
    }

    @Bean
    public AddressMapper addressMapper() {
        return new AddressMapperImpl();
    }


}
