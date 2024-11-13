package com.logant.BookingSystem.Service;

import com.logant.BookingSystem.Dto.ProfileDto;
import com.logant.BookingSystem.Entity.User;
import com.logant.BookingSystem.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userInfoRepo;

    public ProfileDto findById(Long id) {
        User userInfoEntity = userInfoRepo.findById(id)
                .orElseThrow(() -> {
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND ");
                });

        return new ProfileDto(userInfoEntity.getUserName(), userInfoEntity.getEmailId(),
                userInfoEntity.getMobileNumber(), userInfoEntity.getRoles());
    }

    public Optional<User> findByEmail(String email) {
        return userInfoRepo.findByEmailId(email);
    }
}
