package com.cts.fds.service.impl;
import com.cts.fds.dto.*;
import com.cts.fds.entity.Customer;
import com.cts.fds.entity.Restaurant;
import com.cts.fds.exception.ResourceNotFoundException;
import com.cts.fds.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
public class AuthRestaurantService {

    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    JwtRestaurantService jwtRestaurantService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    public ResponseEntity<RestaurantResponseDTO> register(RestaurantCreateDTO request) {


        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        restaurant.setOwnerName(request.getOwnerName());
        restaurant.setPassword(passwordEncoder.encode(request.getPassword())); // Storing plain text password
        restaurant.setEmail(request.getEmail());
        restaurant.setPhone(request.getPhone());
        restaurant.setAddress(request.getAddress());
        restaurant.setRegisteredDate(LocalDateTime.now());

        try{
            restaurantRepository.save(restaurant);
        }
        catch (Exception e)
        {
            log.error("Oops! Already a user");
            return ResponseEntity.badRequest().body(new RestaurantResponseDTO(restaurant.getId(),restaurant.getName(),restaurant.getOwnerName(),restaurant.getEmail(),restaurant.getPhone(),restaurant.getAddress(),"Email already exists","null-token",restaurant.getRegisteredDate()));
        }
        String token= jwtRestaurantService.generateToken(restaurant);
        log.info("User Registration Phase Run Successful");
        return ResponseEntity.ok(new RestaurantResponseDTO(restaurant.getId(),restaurant.getName(),restaurant.getOwnerName(),restaurant.getEmail(),restaurant.getPhone(),restaurant.getAddress(),token,"Registration Successfull",restaurant.getRegisteredDate()));
    }


    public ResponseEntity<LoginResponseDTO> login(RestaurantLoginDTO request) {

        try{
            Restaurant restaurant = restaurantRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
            String token= jwtRestaurantService.generateToken(restaurant);
            log.info("User Login Phase Run Successful");
            return ResponseEntity.ok(new LoginResponseDTO("Login successful as User" ,restaurant.getId(),token));

        }
        catch(Exception e){
            log.error("Oops! Invalid Credentials"+e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO("Invalid email or password",null,"null-token"));

        }

    }
}
