package com.example.onlinestorespringboot.mapper;

import com.example.onlinestorespringboot.dto.OrderItemDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.model.Order;
import com.example.onlinestorespringboot.model.OrderItem;
import com.example.onlinestorespringboot.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface OrderItemMapper {

    ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "order",target = "orderId")
    OrderItemDto toDTO(OrderItem order);

    @Mapping(source = "orderId",target = "order")
    OrderItem toEntity(OrderItemDto orderItemDto);

    default Product mapProductDtoToProduct(ProductDto productDto){
        return productMapper.toEntity(productDto);
    }

    default ProductDto mapProductToProductDto(Product product){
        return productMapper.toDTO(product);
    }

    default Order mapOrderFromOrderItemDtoToOrderItem(Long value){
        return Order.builder().id(value).build();
    }

    default Long mapOrderFromOrderItemDtoToOrderItem(Order order){
        return order.getId();
    }
}
