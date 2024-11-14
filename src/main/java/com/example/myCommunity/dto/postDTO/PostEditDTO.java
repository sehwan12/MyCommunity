package com.example.myCommunity.dto.postDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostEditDTO {
    @NotBlank(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotBlank(message = "게시물 ID는 필수입니다.")
    private Long postId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 1, max = 50, message = "제목은 1자 이상 50자 이하이어야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String postText;
}
