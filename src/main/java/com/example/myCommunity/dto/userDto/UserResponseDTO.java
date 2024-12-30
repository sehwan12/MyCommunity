package com.example.myCommunity.dto.userDto;

import com.example.myCommunity.domain.UserGrade;
import com.example.myCommunity.domain.Users;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
public class UserResponseDTO {

    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private UserGrade userGrade;
    // 필요한 필드 추가

    /**
     * 도메인 엔티티를 DTO로 변환하는 메소드
     * @param user 도메인 엔티티
     * @return UserResponseDTO
     */
    public static UserResponseDTO fromEntity(Users user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .userName(user.getUsername())
                .userEmail(user.getUserEmail())
                .userPhone(user.getUserPhone())
                .userGrade(user.getUserGrade())
                .build();
    }
}
