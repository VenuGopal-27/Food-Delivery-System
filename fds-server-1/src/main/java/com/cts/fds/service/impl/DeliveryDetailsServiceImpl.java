package com.cts.fds.service.impl;
import com.cts.fds.repository.DeliveryAgentRepository;
import com.cts.fds.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cts.fds.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
public class DeliveryDetailsServiceImpl implements UserDetailsService {
    private final DeliveryAgentRepository deliveryAgentRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return deliveryAgentRepository.findByEmail(email)
                .map(user-> User.withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles("DeliveryAgent")
                        .build()
                ).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }
}
