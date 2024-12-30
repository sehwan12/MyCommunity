package com.example.myCommunity.dto.commentDto;

import com.example.myCommunity.domain.Comment;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentRequestDTO {
    @NotBlank
    private String content;

    private Long parentId;

    private Long userId;

    private Long postId;

    //dto to entity
    public Comment toEntity(){
        return Comment.builder()
                .commentText(content)
                .build();
    }
}
