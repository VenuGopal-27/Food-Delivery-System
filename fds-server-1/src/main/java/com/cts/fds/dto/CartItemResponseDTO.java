package com.cts.fds.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartItemResponseDTO {
    private Long cartItemId;
    private Long foodItemId;
    private String foodItemName;
    private double foodItemPrice;
    private int quantity;
}