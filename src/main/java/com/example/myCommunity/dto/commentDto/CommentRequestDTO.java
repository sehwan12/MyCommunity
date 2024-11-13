package com.example.myCommunity.dto.commentDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequestDTO {
    @NotBlank
    private String content;

    private Long parentId;

    private Long userId;
}
