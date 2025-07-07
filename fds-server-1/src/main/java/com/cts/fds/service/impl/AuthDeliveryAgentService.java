package com.cts.fds.service.impl;
import com.cts.fds.dto.*;
import com.cts.fds.entity.Customer;
import com.cts.fds.entity.DeliveryAgent;
import com.cts.fds.exception.ResourceNotFoundException;
import com.cts.fds.repository.CustomerRepository;
import com.cts.fds.repository.DeliveryAgentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.PasswordAuthentication;

@Service
@Slf4j
public class AuthDeliveryAgentService {

    @Autowired
    DeliveryAgentRepository deliveryAgentRepository;
    @Autowired
    JwtDeliveryService jwtDeliveryService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthenticationManager authenticationManager;
    public ResponseEntity<DeliveryAgentResponseDTO> register(DeliveryAgentCreateDTO request) {


        DeliveryAgent agent = new DeliveryAgent();
        agent.setName(request.getName());
        agent.setPhone(request.getPhone());
        agent.setEmail(request.getEmail());
        agent.setAddress(request.getAddress());
        agent.setAge(request.getAge());
        agent.setPassword(passwordEncoder.encode(request.getPassword()));
        agent.setAvailabilityStatus(DeliveryAgent.AvailabilityStatus.AVAILABLE);

        try{
            deliveryAgentRepository.save(agent);
        }
        catch (Exception e)
        {
            log.error("Oops! Already a user");
            return ResponseEntity.badRequest().body(new DeliveryAgentResponseDTO(agent.getId(),agent.getName(),agent.getPhone(),agent.getEmail(), agent.getAddress(), agent.getAge(),agent.getAvailabilityStatus(),"Email already exists","null-token"));
        }
        String token= jwtDeliveryService.generateToken(agent);
        log.info("User Registration Phase Run Successful");
        return ResponseEntity.ok(new DeliveryAgentResponseDTO(agent.getId(),agent.getName(),agent.getPhone(),agent.getEmail(),agent.getAddress(),agent.getAge(),agent.getAvailabilityStatus(),"User registered successfully as Customer",token));
    }


    public ResponseEntity<LoginResponseDTO> login(DeliveryAgentLoginDTO request) {

        try{
            DeliveryAgent agent = deliveryAgentRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
                String token= jwtDeliveryService.generateToken(agent);
            log.info("User Login Phase Run Successful");
            return ResponseEntity.ok(new LoginResponseDTO("Login successful as User",agent.getId() ,token));

        }
        catch(Exception e){
            log.error("Oops! Invalid Credentials"+e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO("Invalid email or password",null,"null-token"));

        }

    }
}
