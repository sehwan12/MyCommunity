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
@AllArgsConstructor
@Table(name = "users")
public class User {

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

    public void updateMember(UserUpdateDTO userUpdateDTO){
        this.userPassword = userUpdateDTO.getUserPassword();
        this.userPhone = userUpdateDTO.getUserPhone();
        this.birthdate = userUpdateDTO.getBirthdate();
        this.username = userUpdateDTO.getUsername();
    }

    @Builder
    public User(String username, String userPassword, UserGrade userGrade, String userPhone, LocalDate birthdate, String userEmail) {
        this.username = username;
        this.userPassword = userPassword;
        this.userGrade = userGrade;
        this.userPhone = userPhone;
        this.birthdate = birthdate;
        this.userEmail = userEmail;
    }

//    // User가 작성한 게시글 목록 (1:N)
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private List<Post> posts;
//
//    // User가 작성한 댓글 목록 (1:N)
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
//    private List<Comment> comments;
//
//    //User가 준 좋아요 목록
//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Heart> hearts;


}

