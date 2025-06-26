package com.cts.fds.service;

import com.cts.fds.dto.OrderCreateDTO;
import com.cts.fds.dto.OrderResponseDTO;

public interface OrderService {
    OrderResponseDTO placeOrder(OrderCreateDTO orderCreateDTO);
    OrderResponseDTO getOrderById(Long orderId);
}