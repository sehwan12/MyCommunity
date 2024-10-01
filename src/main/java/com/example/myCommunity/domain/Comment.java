package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Document와의 다대일 관계 (N:1)
    @ManyToOne
    @JoinColumn(name = "doc_id", nullable = false)
    private Document document;

    @Column(name = "comment_generated", nullable = false)
    private LocalDateTime commentGenerated;

    @Column(name = "comment_modified", nullable = false)
    private LocalDateTime commentModified;

    @Column(name = "comment_text", length = 500, nullable = false)
    private String commentText;

    // 부모 댓글 (자기 자신과의 다대일 관계)
    @ManyToOne
    @JoinColumn(name = "comment_parent")
    private Comment parent;

    // 자식 댓글 목록 (1:N)
    @OneToMany(mappedBy = "parent")
    private List<Comment> childComments;
}
