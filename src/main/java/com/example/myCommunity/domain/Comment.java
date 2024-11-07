package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue
    private Long commentId;

    // User와의 다대일 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Document와의 다대일 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

//    @Embedded
//    private TimeStamp timestamp;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private String commentText;

    // 부모 댓글 (자기 자신과의 다대일 관계)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 자식 댓글 목록 (1:N)
    @OneToMany(mappedBy = "parent")
    private List<Comment> child= new ArrayList<>();

//    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
//    private List<CommentHeart> likes; // 댓글 좋아요 목록
}
