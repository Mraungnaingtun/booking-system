package com.logant.BookingSystem.Security.UserDetails;

import com.logant.BookingSystem.Dto.UserRegistrationDto;
import com.logant.BookingSystem.Entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInfoMapper {

    private final PasswordEncoder passwordEncoder;

    public User convertToEntity(UserRegistrationDto userRegistrationDto) {
        User userInfoEntity = new User();
        userInfoEntity.setUserName(userRegistrationDto.userName());
        userInfoEntity.setEmailId(userRegistrationDto.userEmail());
        userInfoEntity.setMobileNumber(userRegistrationDto.userMobileNo());
        userInfoEntity.setRoles(userRegistrationDto.userRole());
        userInfoEntity.setPassword(passwordEncoder.encode(userRegistrationDto.userPassword()));
        return userInfoEntity;
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
}
