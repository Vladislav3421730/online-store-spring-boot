package com.example.onlinestorespringboot.mapper;

import com.example.onlinestorespringboot.dto.AddressDto;
import com.example.onlinestorespringboot.model.Address;
import org.mapstruct.Mapper;

@Mapper
public interface AddressMapper {

    Address toEntity(AddressDto addressDto);

    AddressDto toDTO(Address address);
}
