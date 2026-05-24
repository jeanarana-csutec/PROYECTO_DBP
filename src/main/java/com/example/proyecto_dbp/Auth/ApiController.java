package com.example.proyecto_dbp.Auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ApiController {
    private final AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody AuthRequest R){
        return new ResponseEntity<>(authService.registro(R), HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest R){
        return new ResponseEntity<>(authService.login(R), HttpStatus.OK);
    }
}
