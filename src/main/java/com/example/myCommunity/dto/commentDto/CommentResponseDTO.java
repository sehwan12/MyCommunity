package com.example.myCommunity.dto.commentDto;

import com.example.myCommunity.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long commentId;
    private String commentText;
    private Long userId;
    private String userName;
    private Long postId;
    private Long parentId; // 부모 댓글 ID (대댓글인 경우)
    private LocalDateTime createdDate;
    private List<CommentResponseDTO> replies; // 대댓글 목록

    /**
     * 도메인 엔티티를 DTO로 변환하는 메소드
     * @param comment 도메인 엔티티
     * @return CommentResponseDTO
     */
    public static CommentResponseDTO fromEntity(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .commentText(comment.getCommentText())
                .userId(comment.getUser().getUserId())
                .postId(comment.getPost().getPostId())
                .userName(comment.getUser().getUserName())
                .parentId(comment.getParent() != null ? comment.getParent().getCommentId() : null)
                .createdDate(comment.getCreatedDate())
                .replies(comment.getReplies().stream()
                        .map(CommentResponseDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}
