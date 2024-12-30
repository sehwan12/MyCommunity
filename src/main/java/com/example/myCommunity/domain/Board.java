package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;

//게시판 클래스
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long boardId;

    private String boardName;

}

