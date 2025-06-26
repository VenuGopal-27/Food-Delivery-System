package com.cts.fds.controller;

import com.cts.fds.dto.DeliveryAgentCreateDTO;
import com.cts.fds.dto.DeliveryAgentLoginDTO;
import com.cts.fds.dto.DeliveryAgentResponseDTO;
import com.cts.fds.dto.LoginResponseDTO;
import com.cts.fds.service.DeliveryAgentService;
import com.cts.fds.service.impl.AuthDeliveryAgentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/deliveryagents")
public class AuthDeliveryController {
    @Autowired
private AuthDeliveryAgentService deliveryAgentService;


    @PostMapping("/register")
    public ResponseEntity<DeliveryAgentResponseDTO> registerDeliveryAgent(@Valid @RequestBody DeliveryAgentCreateDTO agentCreateDTO) {

        return  deliveryAgentService.register(agentCreateDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginDeliveryAgent(@Valid @RequestBody DeliveryAgentLoginDTO agentLoginDTO) {
        return deliveryAgentService.login(agentLoginDTO);
    }
}
