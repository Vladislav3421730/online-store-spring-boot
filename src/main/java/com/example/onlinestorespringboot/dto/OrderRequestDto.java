package com.example.onlinestorespringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "DTO for order request details")
public class OrderRequestDto {

    @Schema(description = "Address details for the order", required = true)
    @NotNull(message = "Address must be not null")
    private AddressDto address;

    @Schema(description = "Total cost of the order", example = "150.50", required = true)
    @NotNull(message = "Total coast must be not null")
    private Double totalCoast;
}