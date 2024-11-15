package com.logant.BookingSystem.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logant.BookingSystem.Dto.AuthRequest;
import com.logant.BookingSystem.Dto.AuthResponse;
import com.logant.BookingSystem.Dto.RegisterReq;
import com.logant.BookingSystem.Service.AuthService;
import com.logant.BookingSystem.exception.MainException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Module", description = "User can register and login (Here not need JWT)")
public class AuthController {

    private final AuthService service;

    @Operation(summary = "Register New User", description = "You need to provide required data")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody RegisterReq request
    ) throws MainException {
return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Login registered User", description = "You need to provide email and password")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(
            @RequestBody AuthRequest request
    ) throws MainException {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
    