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
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    // Document와의 다대일 관계 (N:1)
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column
    //(name = "created_date") 카멜케이스인 경우 대문자를 _소문자 형태로 변환
    @CreatedDate
    private LocalDateTime createdDate;

    @Column
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    private String commentText;

    // 부모 댓글 (자기 자신과의 다대일 관계)
    @Setter
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    // 자식 댓글 목록 (1:N)
    @Builder.Default
    @OneToMany(mappedBy = "parent")
    private List<Comment> replies = new ArrayList<>();

    //대댓글 추가
    public void addReply(Comment reply) {
        this.replies.add(reply);
        reply.setParent(this);
    }

    //댓글 내용 수정 메소드
    public void editContent(String newContent) {
        this.commentText = newContent;
    }

    //작성자 확인 메소드
    public boolean isAuthor(Long userId) {
        return this.user.getUserId().equals(userId);
    }

    @Builder
    public Comment(Users user, Comment parent, Post post,  String commentText) {
        this.user = user;
        this.post = post;
        this.parent=parent;
        this.commentText = commentText;
    }

}
