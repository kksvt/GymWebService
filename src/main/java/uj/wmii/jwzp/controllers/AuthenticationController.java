package uj.wmii.jwzp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import uj.wmii.jwzp.auth.AuthenticationRequest;
import uj.wmii.jwzp.auth.AuthenticationResponse;
import uj.wmii.jwzp.auth.AuthenticationService;
import uj.wmii.jwzp.auth.RegisterRequest;

import java.time.ZoneId;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService service;

    @Autowired
    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            ZoneId zoneId,
            @RequestBody RegisterRequest request
    ) {
         return ResponseEntity.ok(service.register(zoneId, request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
