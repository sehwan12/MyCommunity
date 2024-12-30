package com.example.myCommunity.dto.postDTO;

import com.example.myCommunity.domain.Board;
import com.example.myCommunity.domain.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class PostRegistrationDTO {
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
    @NotBlank
    private String postText;
    @NotNull
    private Long userId;
    private String boardName;

}
