package com.example.myCommunity.domain;

import com.example.myCommunity.domain.like.Heart;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name="user_password", length= 255, nullable = false)
    @Size(min=8, max=20)
    private String userPassword;

    @Column(name = "user_grade", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserGrade userGrade;

    @Column(name = "user_phone", length = 15, nullable = false)
    @Size(min=10, max=15)
    private String userPhone;

    @Column(name = "user_email", length = 255, nullable = false, unique = true)
    @Email
    private String userEmail;

    @Column(name="user_birth")
    private LocalDate birthdate;

    // User가 작성한 게시글 목록 (1:N)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts;

    // User가 작성한 댓글 목록 (1:N)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comment> comments;

    //User가 준 좋아요 목록
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Heart> hearts;


}

