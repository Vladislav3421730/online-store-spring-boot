package com.example.onlinestorespringboot.mapper;


import com.example.onlinestorespringboot.model.Cart;
import com.example.onlinestorespringboot.model.Order;
import com.example.onlinestorespringboot.model.OrderItem;
import com.example.onlinestorespringboot.model.Product;
import com.example.onlinestorespringboot.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OderItemCartMapper {

    ProductRepository productRepository;

    public OrderItem map(Cart cart, Order order){
        Product product = cart.getProduct();
        product.setAmount(cart.getProduct().getAmount() - cart.getAmount());
        productRepository.save(product);
        return OrderItem.builder()
                .product(cart.getProduct())
                .amount(cart.getAmount())
                .order(order)
                .build();
    }
}
