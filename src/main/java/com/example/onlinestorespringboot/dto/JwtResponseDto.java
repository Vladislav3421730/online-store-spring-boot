package com.example.onlinestorespringboot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDto {
    private String accessToken;
    private String refreshToken;
}
