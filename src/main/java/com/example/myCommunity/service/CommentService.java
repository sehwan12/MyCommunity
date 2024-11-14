package com.example.myCommunity.service;

import com.example.myCommunity.Exception.CommentNotFoundException;
import com.example.myCommunity.Exception.PostNotFoundException;
import com.example.myCommunity.Exception.UnauthorizedException;
import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.Comment;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.User;
import com.example.myCommunity.dto.commentDto.CommentEditDTO;
import com.example.myCommunity.dto.commentDto.CommentRequestDTO;
import com.example.myCommunity.repository.CommentRepository;
import com.example.myCommunity.repository.PostRepository;
import com.example.myCommunity.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    @Autowired
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //차후에 스프링 시큐리티 적용
    private User getCurrentUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    //댓글 추가
    @Transactional
    public Comment addComment(Long postId, CommentRequestDTO request) {
        Long userId = request.getUserId(); // DTO에서 사용자 ID 가져오기
        String content = request.getContent();
        Long parentId = request.getParentId();

        Post post=postRepository.findById(postId).orElseThrow(()-> new PostNotFoundException("게시글을 찾을 수 없습니다."));

        User currentUser=getCurrentUser(userId);

        Comment parentComment= null;
        if(parentId!=null){
            parentComment=commentRepository.findById(parentId).orElseThrow(()-> new CommentNotFoundException("부모 댓글을 찾을 수 없습니다."));
            if(!parentComment.getPost().getPostId().equals(postId)){
                throw new IllegalArgumentException("부모 댓글이 다른 게시글에 속해 있습니다.");
            }
        }

        Comment comment= Comment.builder()
                .user(currentUser)
                .post(post)
                .parent(parentComment)
                .createdDate(LocalDateTime.now())
                .commentText(content)
                .build();

        Comment savedComment= commentRepository.save(comment);
        if(parentComment!=null){
            parentComment.addReply(savedComment);
            commentRepository.save(parentComment);
        }
        return savedComment;
    }
    //댓글 수정
    @Transactional
    public Comment editComment(Long commentId, Long currentUserId, CommentEditDTO editDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        // 작성자 ID와 현재 사용자 ID를 직접 비교
        if (!comment.getUser().getUserId().equals(currentUserId)) {
            throw new UnauthorizedException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setCommentText(editDTO.getContent());

        return commentRepository.save(comment);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

        // 작성자 ID와 현재 사용자 ID를 직접 비교
        if (!comment.getUser().getUserId().equals(currentUserId)) {
            throw new UnauthorizedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    //특정 게시글의 모든 최상위 댓글 조회
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Long postId) {
        Post post=postRepository.findById(postId).orElseThrow(
                ()->new PostNotFoundException("게시글을 찾을 수 없습니다.")
        );
        return commentRepository.findByPostAndParentCommentIsNull(post);
    }
}
