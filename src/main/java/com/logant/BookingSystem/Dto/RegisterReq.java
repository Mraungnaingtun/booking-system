package com.logant.BookingSystem.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterReq {
    private String userName;
    private String email;
    private String password;
    private String mobile;
}