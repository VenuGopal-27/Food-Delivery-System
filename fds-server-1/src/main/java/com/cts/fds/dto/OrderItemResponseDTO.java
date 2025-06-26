package com.cts.fds.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class OrderItemResponseDTO {
    private Long foodItemId;
    private String foodItemName;
    private double foodItemPrice;
    private int quantity;
}
