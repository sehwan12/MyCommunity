package com.example.myCommunity.dto.commentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class CommentEditDTO {

    @NotBlank(message = "수정할 댓글 내용은 필수입니다.")
    private String content;
}
