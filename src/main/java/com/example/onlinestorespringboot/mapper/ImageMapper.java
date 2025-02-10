package com.example.onlinestorespringboot.mapper;


import com.example.onlinestorespringboot.dto.CreateImageDto;
import com.example.onlinestorespringboot.dto.ImageDto;
import com.example.onlinestorespringboot.model.Image;
import com.example.onlinestorespringboot.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
public interface ImageMapper {

    @Mapping(source = "product",target = "productId")
    ImageDto toDTO(Image image);

    @Mapping(source = "productId",target = "product")
    Image toEntity(ImageDto imageDto);

    Image toNewEntity(CreateImageDto createImageDto);

    default Long mapProductFromImageToImageDto(Product product){
        return product.getId();
    }

    default Product mapProductFromImageDtoToImage(Long value){
        return Product.builder().id(value).build();
    }


}
