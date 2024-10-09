package com.example.myCommunity.domain;

import com.example.myCommunity.domain.like.CommentHeart;
import com.example.myCommunity.domain.like.PostHeart;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private int postId;

    // User와의 다대일 관계 (N:1)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Board와의 다대일 관계 (N:1)
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Board board;

    @Column(name = "post_timestamp", nullable = false)
    @Embedded
    private TimeStamp post_timestamp;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "post_text", length = 1000, nullable = false)
    private String postText;

    // 첨부파일 목록 (1:N)
    @OneToMany(mappedBy = "post",fetch = FetchType.LAZY)
    private List<Attachment> attachments;

    // 게시글의 댓글 목록 (1:N)
    @OneToMany(mappedBy = "post",  fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostHeart> likes; // 댓글 좋아요 목록
}

