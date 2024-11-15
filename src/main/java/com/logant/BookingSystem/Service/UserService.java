package com.logant.BookingSystem.Service;

import com.logant.BookingSystem.Dto.ChangePasswordDto;
import com.logant.BookingSystem.Dto.ProfileDto;
import com.logant.BookingSystem.Dto.ResetPasswordDto;
import com.logant.BookingSystem.Entity.User;
import com.logant.BookingSystem.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userInfoRepo;
    private final PasswordEncoder passwordEncoder;

    public ProfileDto findById(Long id) {
        User userInfoEntity = userInfoRepo.findById(id)
                .orElseThrow(() -> {
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND ");
                });

        return new ProfileDto(userInfoEntity.getUsername(), userInfoEntity.getEmail(),
                userInfoEntity.getMobileNumber(), String.valueOf(userInfoEntity.getRole()));
    }

    public Optional<User> findByEmail(String email) {
        return userInfoRepo.findByEmail(email);
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


    public String changePassword(ChangePasswordDto dto) throws Exception {

        Optional<User> user = userInfoRepo.findById(dto.getUserId());
        if (!user.isPresent()) {
            throw new Exception("User Not Found");
        }
        User foundUser = user.get();

        if (!passwordEncoder.matches(dto.getOldPassword(), foundUser.getPassword())) {
            throw new Exception("Password Incorrect");
        }

        foundUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userInfoRepo.save(foundUser);
        return "Password Changed Successfully";
    }

}
