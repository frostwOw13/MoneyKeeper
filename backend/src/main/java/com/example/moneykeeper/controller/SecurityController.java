package com.example.moneykeeper.controller;

import com.example.moneykeeper.JwtCore;
import com.example.moneykeeper.entity.User;
import com.example.moneykeeper.record.ErrorRecord;
import com.example.moneykeeper.record.UserDetailsRecord;
import com.example.moneykeeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import java.util.ArrayList;
import java.util.List;

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
        List<String> errors = new ArrayList<>();

        if (request.username() == null || request.username().isBlank()) {
            errors.add("Username should be present");
        }

        if (request.email() == null || request.email().isBlank()) {
            errors.add("Email should be present");
        }

        if (request.password() == null || request.password().isBlank()) {
            errors.add("Password should be present");
        }

        if (userRepository.existsUserByUsername(request.username())) {
            errors.add("Choose different username");
        }

        if (userRepository.existsUserByEmail(request.email())) {
            errors.add("Choose different email");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorRecord(errors));
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String jwt = jwtCore.generateToken(authentication);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jwt);
    }

    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody UserDetailsRecord request) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtCore.generateToken(authentication);

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jwt);
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorRecord(List.of(e.getMessage())));
        }
    }
}
