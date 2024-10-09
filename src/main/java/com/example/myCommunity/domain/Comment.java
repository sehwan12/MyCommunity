package com.example.myCommunity.domain;

import com.example.myCommunity.domain.like.CommentHeart;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int commentId;

    // User와의 다대일 관계 (N:1)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Document와의 다대일 관계 (N:1)
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Embedded
    private TimeStamp timestamp;

    @Column(name = "comment_text", length = 500, nullable = false)
    private String commentText;

    // 부모 댓글 (자기 자신과의 다대일 관계)
    @ManyToOne
    @JoinColumn(name = "comment_parent")
    private Comment parent;

    // 자식 댓글 목록 (1:N)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Comment> childComments;

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CommentHeart> likes; // 댓글 좋아요 목록
}
