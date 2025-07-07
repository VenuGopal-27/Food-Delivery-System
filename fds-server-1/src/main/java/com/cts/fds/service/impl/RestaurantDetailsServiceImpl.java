package com.cts.fds.service.impl;
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
public class RestaurantDetailsServiceImpl implements UserDetailsService {
    private final RestaurantRepository restaurantRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return restaurantRepository.findByEmail(email)
                .map(user-> User.withUsername(user.getEmail())
                        .password(user.getPassword())
                        .roles("Restaurant")
                        .build()
                ).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }
}
