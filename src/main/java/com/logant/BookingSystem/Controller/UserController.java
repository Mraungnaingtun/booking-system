package com.logant.BookingSystem.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logant.BookingSystem.Dto.ChangePasswordDto;
import com.logant.BookingSystem.Dto.ProfileDto;
import com.logant.BookingSystem.Dto.ResetPasswordDto;
import com.logant.BookingSystem.Service.AuthService;
import com.logant.BookingSystem.Service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Module", description = "Operations related to user management")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Operation(summary = "User Profile", description = "Get User Profile")
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getPackageById(@PathVariable Long userId) {
        try {
            ProfileDto profile = userService.findById(userId);
            return ResponseEntity.ok(Map.of("status", "success", "data", profile));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @Operation(summary = "Change Password", description = "This endpoint is to change user password")
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        try {
            String result = authService.changePassword(dto);
            return ResponseEntity.ok(Map.of("status", "success", "message", result));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @Operation(summary = "Rest Password", description = "This endpoint is to reset Password")
    @PreAuthorize("hasAnyAuthority('SCOPE_READ')")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        try {
            String result = authService.resetPassword(dto);
            return ResponseEntity.ok(Map.of("status", "success", "message", result));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

}
