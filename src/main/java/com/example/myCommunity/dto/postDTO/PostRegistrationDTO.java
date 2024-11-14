package com.example.myCommunity.dto.postDTO;

import com.example.myCommunity.domain.Board;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRegistrationDTO {
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
    @NotBlank
    private String postText;

    private Long userId;
    private Board board;

}
