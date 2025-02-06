package com.example.onlinestorespringboot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto {
    private Long id;
    private String region;
    private String town;
    private String exactAddress;
    private String postalCode;
}
