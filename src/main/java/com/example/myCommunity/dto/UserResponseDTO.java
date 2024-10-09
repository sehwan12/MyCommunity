package com.example.myCommunity.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private int userId;
    private String userEmail;
    private String userPhone;
    private String userGrade;
    private LocalDate birthdate;
    // 필요한 필드 추가
}
