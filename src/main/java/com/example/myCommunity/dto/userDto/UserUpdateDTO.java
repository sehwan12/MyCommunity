package com.example.myCommunity.dto.userDto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserUpdateDTO {
    private Long userId;

    @Size(min=8, max=100,message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String userPassword;

    @Size(min=10, max=15,message = "전화번호는 10자리 이상 15자리 이하여야 합니다.")
    private String userPhone;

    private LocalDate birthdate;

    private String userName;
}
