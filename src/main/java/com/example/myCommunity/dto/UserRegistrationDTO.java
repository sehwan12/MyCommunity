package com.example.myCommunity.dto;

import com.example.myCommunity.domain.UserGrade;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "유효한 이메일 주소를 입력하세요")
    private String userEmail;

    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    @Size(min=8, max=100,message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String userPassword;

    @NotBlank(message = "전화번호는 필수 입력 사항입니다.")
    @Size(min=10, max=15,message = "전화번호는 10자리 이상 15자리 이하여야 합니다.")
    private String userPhone;

    @NotBlank(message= "생년월일은 필수 입력 사항입니다")
    private LocalDate birthdate;

    @NotBlank
    private UserGrade userGrade;



}
