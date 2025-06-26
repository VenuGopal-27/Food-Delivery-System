package com.cts.fds.controller;

import com.cts.fds.dto.*;
import com.cts.fds.service.impl.AuthRestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.fds.service.impl.AuthCustomerService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/auth/restaurant")
public class AuthRestaurantController {
    @Autowired private AuthRestaurantService authRestaurantService;

    @PostMapping("/register")
    public ResponseEntity<RestaurantResponseDTO> registerCustomer(@Valid @RequestBody RestaurantCreateDTO restaurantCreateDTO) {

        return authRestaurantService.register(restaurantCreateDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginCustomer(@Valid @RequestBody RestaurantLoginDTO restaurantLoginDTO) {
        return authRestaurantService.login(restaurantLoginDTO);
    }


}
