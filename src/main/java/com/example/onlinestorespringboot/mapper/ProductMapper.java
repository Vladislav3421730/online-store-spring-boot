package com.example.onlinestorespringboot.mapper;

import com.example.onlinestorespringboot.dto.CreateProductDto;
import com.example.onlinestorespringboot.dto.ImageDto;
import com.example.onlinestorespringboot.dto.ProductDto;
import com.example.onlinestorespringboot.model.Image;
import com.example.onlinestorespringboot.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface ProductMapper {

    ImageMapper imageMapper = Mappers.getMapper(ImageMapper.class);

    Product toNewEntity(CreateProductDto dto);

    ProductDto toDTO(Product product);

    Product toEntity(ProductDto productDto);

    default List<Image> mapImageDtosToImages(List<ImageDto> images){
        if(images==null){
            return null;
        }
        return images.stream()
                .map(imageMapper::toEntity)
                .collect(Collectors.toList());
    }

    default List<ImageDto> mapImagesToImageDtos(List<Image> images){
        if(images==null){
            return null;
        }
        return images.stream()
                .map(imageMapper::toDTO)
                .collect(Collectors.toList());
    }

}