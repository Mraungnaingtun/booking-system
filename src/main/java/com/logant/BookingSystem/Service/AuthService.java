package com.logant.BookingSystem.Service;

import com.logant.BookingSystem.Dto.AuthResponseDto;
import com.logant.BookingSystem.Dto.ChangePasswordDto;
import com.logant.BookingSystem.Dto.ResetPasswordDto;
import com.logant.BookingSystem.Dto.UserRegistrationDto;
import com.logant.BookingSystem.Entity.User;
import com.logant.BookingSystem.Repository.UserRepository;
import com.logant.BookingSystem.Security.Jwt.JwtTokenGenerator;
import com.logant.BookingSystem.Security.UserDetails.UserInfoMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userInfoRepo;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserInfoMapper userInfoMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDto getJwtTokensAfterAuthentication(Authentication authentication,
            HttpServletResponse response) {
        try {
            var userInfoEntity = userInfoRepo.findByEmailId(authentication.getName())
                    .orElseThrow(() -> {
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND ");
                    });

            String token = jwtTokenGenerator.generateToken(authentication);

            return AuthResponseDto.builder()
                    .token(token)
                    .userName(userInfoEntity.getUserName())
                    .build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please Try Again");
        }
    }

    public AuthResponseDto registerUser(UserRegistrationDto userRegistrationDto,
            HttpServletResponse httpServletResponse) {
        try {

            Optional<User> user = userInfoRepo.findByEmailId(userRegistrationDto.userEmail());
            if (user.isPresent()) {
                throw new Exception("User Already Exist");
            }

            User userDetailsEntity = userInfoMapper.convertToEntity(userRegistrationDto);
            Authentication authentication = createAuthenticationObject(userDetailsEntity);

            // Generate a JWT token
            String token = jwtTokenGenerator.generateToken(authentication);

            User savedUserDetails = userInfoRepo.save(userDetailsEntity);

            emailService.sendVerifyEmail(userDetailsEntity.getEmailId());

            return AuthResponseDto.builder()
                    .token(token)
                    .userName(savedUserDetails.getUserName())
                    .build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public String changePassword(ChangePasswordDto dto) throws Exception {

        Optional<User> user = userInfoRepo.findById(dto.getUserId());
        if (!user.isPresent()) {
            throw new Exception("User Not Found");
        }
        User foundUser = user.get();

        if (!userInfoMapper.checkPassword(dto.getOldPassword(), foundUser.getPassword())) {
            throw new Exception("Password Incorrect");
        }

        foundUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userInfoRepo.save(foundUser);
        return "Password Changed Successfully";
    }


    public String resetPassword(ResetPasswordDto dto) throws Exception {
        Optional<User> user = userInfoRepo.findById(dto.getUserId());
        if (!user.isPresent()) {
            throw new Exception("User Not Found");
        }
        
        User foundUser = user.get();
        
        // Encode and set the new password
        foundUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userInfoRepo.save(foundUser);
        
        return "Password Reset Successfully";
    }
    

    private static Authentication createAuthenticationObject(User userInfoEntity) {
        String username = userInfoEntity.getEmailId();
        String password = userInfoEntity.getPassword();
        String roles = userInfoEntity.getRoles();

        String[] roleArray = roles.split(",");
        GrantedAuthority[] authorities = Arrays.stream(roleArray)
                .map(role -> (GrantedAuthority) role::trim)
                .toArray(GrantedAuthority[]::new);

        return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(authorities));
    }

}
