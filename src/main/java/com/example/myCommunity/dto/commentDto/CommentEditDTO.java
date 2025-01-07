package com.example.myCommunity.dto.commentDto;
import com.example.myCommunity.domain.Comment;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class CommentEditDTO {
    private Long commentId;
    @NotBlank(message = "수정할 댓글 내용은 필수입니다.")
    private String commentText;

    public static CommentEditDTO fromEntity(Comment comment) {
        return CommentEditDTO.builder().
                commentId(comment.getCommentId()).
                commentText(comment.getCommentText()).
                build();

    }
}
