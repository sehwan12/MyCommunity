package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;

//게시판 클래스
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long boardId;

    private String boardName;

//    // 카테고리의 게시글 목록 (1:N)
//    @OneToMany(mappedBy = "board")
//    private List<Post> posts;
}

