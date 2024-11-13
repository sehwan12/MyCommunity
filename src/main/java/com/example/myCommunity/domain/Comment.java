package com.example.myCommunity.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    @Setter
    private String commentText;

    // 부모 댓글 (자기 자신과의 다대일 관계)
    @Setter
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 자식 댓글 목록 (1:N)
    @OneToMany(mappedBy = "parent")
    private List<Comment> replies = new ArrayList<>();

    //대댓글 추가
    public void addReply(Comment reply) {
        replies.add(reply);
        reply.setParent(this);
    }

    public void removeReply(Comment reply) {
        replies.remove(reply);
        reply.setParent(null);
    }

//    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
//    private List<CommentHeart> likes; // 댓글 좋아요 목록
}
