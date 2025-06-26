package com.cts.fds.dto;

import com.cts.fds.entity.Order;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class OrderUpdateStatusDTO {
    @NotNull(message = "New status cannot be null")
    private Order.OrderStatus newStatus;
}
