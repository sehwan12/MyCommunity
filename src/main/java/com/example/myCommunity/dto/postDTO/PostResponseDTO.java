package com.example.myCommunity.dto.postDTO;

import com.example.myCommunity.domain.Board;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.Users;
import lombok.*;

@Data
@Builder
public class PostResponseDTO {
    private Long postId;
    private String title;
    private String postText;
    private Users user;
    private Board board;

    public static PostResponseDTO fromEntity(Post post) {
        return PostResponseDTO.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .postText(post.getPostText())
                .user(post.getUser())
                .board(post.getBoard())
                .build();
    }
}
