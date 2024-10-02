package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
//게시판 클래스
@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private int boardId;

    @Column(name = "board_name", length = 20, nullable = false)
    private String boardName;

    // 카테고리의 게시글 목록 (1:N)
    @OneToMany(mappedBy = "board")
    private List<Post> posts;
}

