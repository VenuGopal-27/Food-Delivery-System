package com.cts.fds.dto;

import com.cts.fds.entity.Order;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDTO {
    @NotNull(message = "Customer ID cannot be null")
    private Long customerId; // Will be taken from path variable in controller, but useful for validation

    @NotBlank(message = "Delivery address cannot be blank")
    private String deliveryAddress;

    @NotNull(message = "Payment type cannot be null")
    private Order.PaymentType paymentType;
}