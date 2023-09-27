package com.example.moneykeeper.controller;

import com.example.moneykeeper.JwtCore;
import com.example.moneykeeper.entity.User;
import com.example.moneykeeper.record.UserDetailsRecord;
import com.example.moneykeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtCore jwtCore;

    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody UserDetailsRecord request) {
        if (userRepository.existsUserByUsername(request.username())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different username");
        }

        if (userRepository.existsUserByEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different email");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String jwt = jwtCore.generateToken(authentication);

        return ResponseEntity.ok(jwt);
    }

    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody UserDetailsRecord request) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);

        return ResponseEntity.ok(jwt);
    }
}
