package me.learning.controller;

import me.learning.model.JwtRequest;
import me.learning.model.JwtResponse;
import me.learning.security.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private JwtHelper helper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {

        // Authenticate user details whether it is valid or not...
        this.doAuthenticate(request.getEmail(), request.getPassword());

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // Generate token
        String token = this.helper.generateToken(userDetails);

        // Build response
        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername())
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, password);

        try {
            manager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid Username or Password!!");
        }
    }
}

