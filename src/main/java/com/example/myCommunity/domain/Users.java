package com.example.myCommunity.domain;

import com.example.myCommunity.dto.userDto.UserUpdateDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long userId;

    private String username;

    @Size(min=8, max=20)
    private String userPassword;

    @Enumerated(EnumType.STRING)
    private UserGrade userGrade;

    @Size(min=10, max=15)
    private String userPhone;

    @NotEmpty
    @Email
    private String userEmail;

    private LocalDate birthdate;

    //dto을 입력받을지 엔티티를 입력받을지..?
    public void updateUser(UserUpdateDTO userUpdateDTO){
        updatePassword(userUpdateDTO);
        updateName(userUpdateDTO);
        updatePhone(userUpdateDTO);
        updateBirth(userUpdateDTO);
    }

    @Builder
    public Users(String username, String userPassword, UserGrade userGrade, String userPhone, LocalDate birthdate, String userEmail) {
        this.username = username;
        this.userPassword = userPassword;
        this.userGrade = userGrade;
        this.userPhone = userPhone;
        this.birthdate = birthdate;
        this.userEmail = userEmail;
    }

    private void updatePassword(UserUpdateDTO userUpdateDTO){
        if(userUpdateDTO.getUserPassword()!=null){
            this.userPassword = userUpdateDTO.getUserPassword();
        }
    }

    private void updatePhone(UserUpdateDTO userUpdateDTO){
        if(userUpdateDTO.getUserPhone()!=null){
            this.userPhone = userUpdateDTO.getUserPhone();
        }
    }

    private void updateBirth(UserUpdateDTO userUpdateDTO){
        if(userUpdateDTO.getBirthdate()!=null){
            this.birthdate = userUpdateDTO.getBirthdate();
        }
    }

    private  void updateName(UserUpdateDTO userUpdateDTO){
        if(userUpdateDTO.getUsername()!=null){
            this.username = userUpdateDTO.getUsername();
        }
    }
}

