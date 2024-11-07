package com.example.myCommunity.trash;

import com.example.myCommunity.domain.UserGrade;
import lombok.*;

import java.time.LocalDate;

@Data
public class UserResponseDTO {

    private Long userId;
    private String userEmail;
    private String userPhone;
    private UserGrade userGrade;
    private LocalDate birthdate;
    // 필요한 필드 추가
}
