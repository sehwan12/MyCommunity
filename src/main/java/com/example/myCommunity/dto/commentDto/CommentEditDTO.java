package com.example.myCommunity.dto.commentDto;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class CommentEditDTO {
    private Long commentId;
    @NotBlank(message = "수정할 댓글 내용은 필수입니다.")
    private String content;
}
