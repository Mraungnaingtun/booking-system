package com.logant.BookingSystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logant.BookingSystem.Dto.ChangePasswordDto;
import com.logant.BookingSystem.Dto.ProfileDto;
import com.logant.BookingSystem.Dto.ResetPasswordDto;
import com.logant.BookingSystem.Dto.ResponseWrapper;
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

    @Operation(summary = "User Profile", description = "Get User Profile")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfileById(@PathVariable Long userId) {
        try {
            ProfileDto profile = userService.findById(userId);
            return ResponseWrapper.success(profile);

        } catch (Exception e) {
            return ResponseWrapper.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Change Password", description = "This endpoint is to change user password")
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto dto) {
        try {
            String result = userService.changePassword(dto);
            return ResponseWrapper.success(result);

        } catch (Exception e) {
            return ResponseWrapper.error(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Rest Password", description = "This endpoint is to reset Password")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        try {
            String ret = userService.resetPassword(dto);
            return ResponseWrapper.success(ret);

        } catch (Exception e) {
            return ResponseWrapper.error(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
