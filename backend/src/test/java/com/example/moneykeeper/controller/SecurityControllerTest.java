package com.example.moneykeeper.controller;

import com.example.moneykeeper.JwtCore;
import com.example.moneykeeper.entity.User;
import com.example.moneykeeper.record.ErrorRecord;
import com.example.moneykeeper.record.UserDetailsRecord;
import com.example.moneykeeper.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityControllerTest {

    @Mock
    JwtCore jwtCore;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    SecurityController controller;

    @Test
    void signup_PayloadUsernameIsInvalid_ReturnsValidResponse() {
        // given
        String errorMessage = "Username should be present";
        UserDetailsRecord request = new UserDetailsRecord("", "example@gmail.com", "password");

        // when
        ResponseEntity<?> response = this.controller.signup(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorRecord(List.of(errorMessage)), response.getBody());
    }

    @Test
    void signup_PayloadEmailIsInvalid_ReturnsValidResponse() {
        // given
        String errorMessage = "Email should be present";
        UserDetailsRecord request = new UserDetailsRecord("username", "", "password");

        // when
        ResponseEntity<?> response = this.controller.signup(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorRecord(List.of(errorMessage)), response.getBody());
    }

    @Test
    void signup_PayloadPasswordIsInvalid_ReturnsValidResponse() {
        // given
        String errorMessage = "Password should be present";
        UserDetailsRecord request = new UserDetailsRecord("username", "example@gmail.com", "");

        // when
        ResponseEntity<?> response = this.controller.signup(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorRecord(List.of(errorMessage)), response.getBody());
    }

    @Test
    void signup_PayloadUsernameIsExists_ReturnsValidResponse() {
        // given
        String errorMessage = "Choose different username";
        UserDetailsRecord request = new UserDetailsRecord("username", "example@gmail.com", "password");

        doReturn(true).when(this.userRepository).existsUserByUsername(request.username());

        // when
        ResponseEntity<?> response = this.controller.signup(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorRecord(List.of(errorMessage)), response.getBody());
    }

    @Test
    void signup_PayloadEmailIsExists_ReturnsValidResponse() {
        // given
        String errorMessage = "Choose different email";
        UserDetailsRecord request = new UserDetailsRecord("username", "example@gmail.com", "password");

        doReturn(true).when(this.userRepository).existsUserByEmail(request.email());

        // when
        ResponseEntity<?> response = this.controller.signup(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorRecord(List.of(errorMessage)), response.getBody());
    }

    @Test
    void signup_PayloadIsInvalidAndIsExists_ReturnsValidResponse() {
        // given
        ErrorRecord errors = new ErrorRecord(List.of(
                "Username should be present",
                "Email should be present",
                "Password should be present",
                "Choose different username",
                "Choose different email"
        ));
        UserDetailsRecord request = new UserDetailsRecord("", "", "");

        doReturn(true).when(this.userRepository).existsUserByUsername(request.username());
        doReturn(true).when(this.userRepository).existsUserByEmail(request.email());

        // when
        ResponseEntity<?> response = this.controller.signup(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(errors, response.getBody());
    }

    @Test
    void signup_PayloadIsValid_ReturnsValidResponse() {
        // given
        UserDetailsRecord request = new UserDetailsRecord("username", "example@gmail.com", "password");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String jwt = jwtCore.generateToken(authentication);

        // when
        ResponseEntity<?> response = this.controller.signup(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.TEXT_PLAIN, response.getHeaders().getContentType());
        assertEquals(jwt, response.getBody());
    }

    @Test
    void signin_PayloadIsValid_ReturnsValidResponse() {
        // given
        UserDetailsRecord request = new UserDetailsRecord("username", "example@gmail.com", "password");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String jwt = jwtCore.generateToken(authentication);

        // when
        ResponseEntity<?> response = this.controller.signin(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.TEXT_PLAIN, response.getHeaders().getContentType());
        assertEquals(jwt, response.getBody());
    }

    @Test
    void signin_PayloadIsInvalid_ReturnsValidResponse() {
        // given
        UserDetailsRecord request = new UserDetailsRecord("username", "example@gmail.com", "password");

        String errorMessage = "Bad credentials";
        doThrow((new BadCredentialsException(errorMessage))).when(this.authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        // when
        ResponseEntity<?> response = this.controller.signin(request);

        // then
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(new ErrorRecord(List.of(errorMessage)), response.getBody());
    }
}