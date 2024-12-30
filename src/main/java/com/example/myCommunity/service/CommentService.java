package com.example.myCommunity.service;

import com.example.myCommunity.Exception.CommentNotFoundException;
import com.example.myCommunity.Exception.PostNotFoundException;
import com.example.myCommunity.Exception.UnauthorizedException;
import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.Comment;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.Users;
import com.example.myCommunity.dto.commentDto.CommentEditDTO;
import com.example.myCommunity.dto.commentDto.CommentRequestDTO;
import com.example.myCommunity.repository.CommentRepository;
import com.example.myCommunity.repository.PostRepository;
import com.example.myCommunity.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 헬퍼 메소드들
    private Users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
    }

    private Comment getParentCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("부모 댓글을 찾을 수 없습니다."));
    }


    //댓글 추가
    @Transactional
    public Long addComment(CommentRequestDTO request) {
        // 사용자 조회
        Users user = getUserById(request.getUserId());

        // 게시글 조회
        Post post = getPostById(request.getPostId());

        // DTO를 엔티티로 변환
        Comment comment = request.toEntity();
        comment.setUser(user);
        comment.setPost(post);

        // 대댓글인 경우 부모 댓글 설정
        if (request.getParentId() != null) {
            Comment parentComment = getParentCommentById(request.getParentId());
            comment.setParent(parentComment);
            parentComment.addReply(comment);
        }

        // 댓글 저장
        commentRepository.save(comment);
        return comment.getCommentId();
    }

    //댓글 수정
    @Transactional
    public void editComment(Long currentUserId, CommentEditDTO editDTO) {
        Comment comment =getCommentById(editDTO.getCommentId());
        // 작성자 ID와 현재 사용자 ID를 직접 비교
        if (!comment.isAuthor(currentUserId)) {
            throw new UnauthorizedException("댓글을 수정할 권한이 없습니다.");
        }

        comment.editContent(editDTO.getContent());

    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = getCommentById(commentId);

        if (!comment.isAuthor(currentUserId)) {
            throw new UnauthorizedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    //특정 게시글의 모든 최상위 댓글 조회
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        Post post=getPostById(postId);
        return commentRepository.findByPostAndParentIsNull(post);
    }


    //특정 댓글의 모든 대댓글 조회
    @Transactional(readOnly = true)
    public List<Comment> getRepliesByParentCommentId(Long parentCommentId) {
        Comment parentComment = getParentCommentById(parentCommentId);
        return commentRepository.findByParent(parentComment);
    }
}
