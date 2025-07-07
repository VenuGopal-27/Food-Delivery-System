package com.cts.fds.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartResponseDTO {
    private Long cartId;
    private Long customerId;
    private List<CartItemResponseDTO> cartItems;
    private double totalCartValue;
}
