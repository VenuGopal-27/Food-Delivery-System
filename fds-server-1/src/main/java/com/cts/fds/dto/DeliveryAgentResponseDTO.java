package com.cts.fds.dto;

import com.cts.fds.entity.DeliveryAgent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryAgentResponseDTO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private int age;
    private DeliveryAgent.AvailabilityStatus availabilityStatus;
    private String message;
    private String token;
}