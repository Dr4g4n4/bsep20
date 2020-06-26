package com.example.bsep.controller;

import com.example.bsep.model.Admin;
import com.example.bsep.model.UserTokenState;
import com.example.bsep.security.TokenUtils;
import com.example.bsep.security.auth.JwtAuthenticationRequest;
import com.example.bsep.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class LoginController {
    @Autowired
    private LoggingService service;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTokenState> loginUser(@RequestBody JwtAuthenticationRequest authenticationRequest,
                                                    HttpServletResponse response) throws AuthenticationException, IOException {
        Admin log = service.loginUser(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        if (log != null) {
            final Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                            authenticationRequest.getPassword()));
            // Ubaci username + password u kontext
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Admin user = (Admin) authentication.getPrincipal();
            String jwt = tokenUtils.generateToken(user.getUsername());
            int expiresIn = tokenUtils.getExpiredIn();
            com.example.bsep.model.Role r = (com.example.bsep.model.Role) user.getRoles().toArray()[0];
            return ResponseEntity.ok(new UserTokenState(jwt, expiresIn, r.getName()));
        } else {
            return new ResponseEntity<UserTokenState>(HttpStatus.NOT_FOUND);
        }

    }
}
