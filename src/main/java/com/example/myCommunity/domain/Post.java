package com.example.myCommunity.domain;

import com.example.myCommunity.dto.postDTO.PostEditDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long postId;

    // User와의 다대일 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    // Board와의 다대일 관계 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column
    @CreatedDate
    private LocalDateTime createdDate;

    @Column
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @Size(min = 1, max = 50)
    private String title;

    private String postText;

    public void updatePost(PostEditDTO edit) {
        updateTitle(edit);
        updateText(edit);
    }

    private void updateTitle(PostEditDTO edit) {
        this.title = edit.getTitle();
    }

    private void updateText(PostEditDTO edit) {
        this.postText = edit.getPostText();
    }

    public boolean isAuthor(Long userId) {
        if(this.user == null || this.user.getUserId() == null || userId == null){
            return false;
        }
        return this.user.getUserId().equals(userId);
    }

}

