package com.cts.fds.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.fds.dto.CustomerCreateDTO;
import com.cts.fds.dto.CustomerLoginDTO;
import com.cts.fds.dto.CustomerResponseDTO;
import com.cts.fds.dto.LoginResponseDTO;
import com.cts.fds.service.impl.AuthCustomerService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/auth/customer")
public class AuthCustomerController {
    @Autowired private AuthCustomerService authCustomerService;

	 @PostMapping("/register")
	    public ResponseEntity<CustomerResponseDTO> registerCustomer(@Valid @RequestBody CustomerCreateDTO customerCreateDTO) {
	        
	        return authCustomerService.register(customerCreateDTO);
	    }
	    
	    @PostMapping("/login")
	    public ResponseEntity<LoginResponseDTO> loginCustomer(@Valid @RequestBody CustomerLoginDTO customerLoginDTO) {
	        return authCustomerService.login(customerLoginDTO);
	    }


}
