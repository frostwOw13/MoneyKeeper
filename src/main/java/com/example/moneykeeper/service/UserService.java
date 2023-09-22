package com.example.moneykeeper.service;

import com.example.moneykeeper.UserDetailsImpl;
import com.example.moneykeeper.entity.User;
import com.example.moneykeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(
                        String.format("User '$s' not found", username)
                ));

        return UserDetailsImpl.build(user);
    }
}
