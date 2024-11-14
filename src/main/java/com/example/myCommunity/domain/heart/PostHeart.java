package com.example.myCommunity.domain.heart;

import com.example.myCommunity.domain.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("P")
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PostHeart extends Heart {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

}
