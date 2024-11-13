package com.logant.BookingSystem.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {

    private String userName;
    private String emailId;
    private String mobileNumber;
    private String role;

}
