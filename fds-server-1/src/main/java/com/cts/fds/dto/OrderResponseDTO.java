package com.cts.fds.dto;

import com.cts.fds.entity.Order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class OrderResponseDTO {
    private Long id;
    private Long customerId;
    private String customerUsername;
    private Long restaurantId;
    private String restaurantName;
    private Order.OrderStatus orderStatus;
    private LocalDateTime orderDate;
    private String deliveryAddress;
    private Order.PaymentType paymentType;
    private double totalAmount;
    private List<OrderItemResponseDTO> orderItems;
    private Long deliveryAgentId;
    private String deliveryAgentName;
}
