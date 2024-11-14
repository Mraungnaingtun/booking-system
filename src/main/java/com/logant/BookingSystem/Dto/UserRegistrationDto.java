package com.logant.BookingSystem.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "DTO for user registration containing user details such as username, password, etc.")
public record UserRegistrationDto(
        @NotEmpty(message = "User Name must not be empty") 
        String userName,

        @NotEmpty(message = "User Phone must not be empty") 
        String userMobileNo,

        @NotEmpty(message = "User email must not be empty") 
        @Email(message = "Invalid email format") 
        String userEmail,

        @NotEmpty(message = "User password must not be empty") 
        String userPassword,

        @NotEmpty(message = "User role must not be empty") 
        String userRole) {
}
