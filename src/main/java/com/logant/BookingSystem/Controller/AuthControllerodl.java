// package com.logant.BookingSystem.Controller;

// import com.logant.BookingSystem.Dto.AuthResponseDto;
// import com.logant.BookingSystem.Dto.LoginRequestDto;
// import com.logant.BookingSystem.Dto.UserRegistrationDto;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.servlet.http.HttpServletResponse;
// import jakarta.validation.Valid;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.support.DefaultMessageSourceResolvable;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.validation.BindingResult;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/old/auth")
// @Tag(name = "Authentication Module", description = "ROLE_ADMIN / ROLE_MANAGER / ROLE_USER")
// public class AuthController {

//     @Autowired
//     private AuthenticationManager authenticationManager;

//     @Operation(summary = "Register New User", description = "You need to provide required data")
//     @PostMapping("/register")
//     public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto userRegistrationDto,
//             BindingResult bindingResult, HttpServletResponse httpServletResponse) {

//         if (bindingResult.hasErrors()) {
//             List<String> errorMessages = bindingResult.getAllErrors().stream()
//                     .map(DefaultMessageSourceResolvable::getDefaultMessage)
//                     .toList();
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages);
//         }

//         try {
//             // Register user
//             // AuthResponseDto response = authService.registerUser(userRegistrationDto, httpServletResponse);
//             return ResponseEntity.ok("");
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
//         }
//     }



//     @Operation(summary = "Login registered User", description = "You need to provide email and password")
//     @PostMapping("/login")
//     public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequestDto,
//             HttpServletResponse response) {
//         try {

//             UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                     loginRequestDto.getUsername(), loginRequestDto.getPassword());

//             // Authenticate the user
//             Authentication authentication = authenticationManager.authenticate(authenticationToken);
//             // AuthResponseDto authResponse = authService.getJwtTokensAfterAuthentication(authentication, response);
//             return ResponseEntity.ok("");

//         } catch (Exception e) {
//             if ("Bad credentials".equals(e.getMessage())) {
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                         .body(Map.of("error", "Wrong credentials", "message", e.getMessage()));
//             }

//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body(Map.of("error", "Server error", "message", e.getMessage()));
//         }
//     }

// }
