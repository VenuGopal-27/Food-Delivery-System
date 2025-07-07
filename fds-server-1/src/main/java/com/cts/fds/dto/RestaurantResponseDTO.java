package com.cts.fds.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponseDTO {
    private Long id;
    private String name;
    private String ownerName;
    private String email;
    private String phone;
    private String address;
    private String token;
    private String message;
    private LocalDateTime registeredDate;
}