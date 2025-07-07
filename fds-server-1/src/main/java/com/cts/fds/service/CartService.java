package com.cts.fds.service;

import com.cts.fds.dto.CartAddItemDTO;
import com.cts.fds.dto.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCartByCustomerId(Long customerId);
    CartResponseDTO addItemToCart(CartAddItemDTO cartAddItemDTO);
    CartResponseDTO updateCartItemQuantity(Long customerId, Long foodItemId, int quantity);
    void removeCartItem(Long customerId, Long foodItemId);
    void clearCart(Long customerId);
}