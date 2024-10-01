package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "user_grade", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserGrade userGrade;

    @Column(name = "user_phone", length = 15, nullable = false)
    private String userPhone;

    @Column(name = "user_email", length = 255, nullable = false)
    private String userEmail;

    @Column(name = "user_age")
    private int userAge;

    // User가 작성한 게시글 목록 (1:N)
    @OneToMany(mappedBy = "user")
    private List<Document> documents;

    // User가 작성한 댓글 목록 (1:N)
    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    public enum UserGrade {
        NORMAL, ADMIN
    }
}

