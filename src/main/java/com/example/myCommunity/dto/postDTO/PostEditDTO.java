package com.example.myCommunity.dto.postDTO;

import com.example.myCommunity.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PostEditDTO {

    @NotBlank(message = "게시물 ID는 필수입니다.")
    private Long postId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 1, max = 50, message = "제목은 1자 이상 50자 이하이어야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String postText;

    public static PostEditDTO fromEntity(Post post) {
        return PostEditDTO.builder().
                postId(post.getPostId()).
                title(post.getTitle()).
                postText(post.getPostText()).
                build();
    }
}
