package com.example.myCommunity.dto.postDTO;

import com.example.myCommunity.domain.*;
import com.example.myCommunity.dto.AttachmentDTO;
import com.example.myCommunity.dto.commentDto.CommentResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PostResponseDTO {
    private Long postId;
    private String title;
    private String postText;
    private Users user;
    private Board board;
    private LocalDateTime createdDate;
    private List<AttachmentDTO> attachments=new ArrayList<>();
    private List<CommentResponseDTO> comments=new ArrayList<>();

    // Entity에서 DTO로 변환하는 메소드
    public static PostResponseDTO fromEntity(Post post, List<Attachment> attachments, List<Comment> comments) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setPostText(post.getPostText());
        dto.setUser(post.getUser());
        dto.setBoard(post.getBoard());
        dto.setCreatedDate(post.getCreatedDate());

        if (attachments != null) {
            dto.setAttachments(
                    attachments.stream()
                            .map(AttachmentDTO::fromEntity)
                            .collect(Collectors.toList())
            );
        }

        if(comments != null) {
            dto.setComments(
                    comments.stream().map(CommentResponseDTO::fromEntity).collect(Collectors.toList())
            );
        }
        return dto;
    }
}
