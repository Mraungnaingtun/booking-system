package com.logant.BookingSystem.Service;


import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.logant.BookingSystem.Dto.AuthRequest;
import com.logant.BookingSystem.Dto.AuthResponse;
import com.logant.BookingSystem.Dto.RegisterReq;
import com.logant.BookingSystem.Entity.User;
import com.logant.BookingSystem.Enum.Role;
import com.logant.BookingSystem.Repository.UserRepository;
import com.logant.BookingSystem.Security.JwtService;
import com.logant.BookingSystem.exception.MainException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    public AuthResponse register(RegisterReq request) throws MainException {
    var user= User.builder()
            .userName(request.getUserName())
            .email(request.getEmail())
            .mobileNumber(request.getMobile())
            .password(passwordEncoder.encode(request.getPassword()))
            .createdAt(LocalDateTime.now())
            .role(Role.USER)
            .build();
            
            repository.save(user);

            var jwtToken = jwtService.generateToken(user);
            return AuthResponse.builder()
                    .token(jwtToken)
                    .build();
    }

    public AuthResponse authenticate(AuthRequest request) throws MainException{
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user=repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}
