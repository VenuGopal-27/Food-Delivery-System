package com.cts.fds.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CartAddItemDTO {
    @NotNull(message = "Customer ID cannot be null")
    private Long customerId; // Will be set by controller for convenience

    @NotNull(message = "Food item ID cannot be null")
    private Long foodItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}