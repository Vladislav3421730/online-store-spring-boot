package com.example.onlinestorespringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@Schema(description = "DTO for standard response message")
public class ResponseDto {

    @Schema(description = "Message in the response", example = "Operation completed successfully")
    private String message;
}
