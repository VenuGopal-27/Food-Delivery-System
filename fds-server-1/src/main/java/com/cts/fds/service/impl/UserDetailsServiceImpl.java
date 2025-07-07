package com.cts.fds.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cts.fds.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final CustomerRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByUsername(email)
                .map(user-> User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles("Customer")
                        .build()
                ).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }
}
