package com.example.myCommunity.domain.heart;

import com.example.myCommunity.domain.Comment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("C")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommentHeart extends Heart {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
