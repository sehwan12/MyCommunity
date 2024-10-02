package com.example.myCommunity.domain.like;

import com.example.myCommunity.domain.Comment;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@DiscriminatorValue("C")
@Getter
public class CommentHeart extends Heart {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
