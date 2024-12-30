package com.example.myCommunity.service;

import com.example.myCommunity.Exception.*;
import com.example.myCommunity.domain.Comment;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.Users;
import com.example.myCommunity.domain.heart.CommentHeart;
import com.example.myCommunity.domain.heart.PostHeart;
import com.example.myCommunity.repository.*;
import org.apache.catalina.User;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class HeartService {
    private final PostHeartRepository postHeartRepository;
    private final CommentHeartRepository commentHeartRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
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

    private PostHeart getPostHeartById(Long userId, Long postId) {
        Post post=getPostById(postId);
        Users user=getUserById(userId);
        return postHeartRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new HeartNotFoundException("하트를 찾을 수 없습니다."));
    }

    CommentHeart getCommentHeartById(Long userId, Long commentId) {
        Users user=getUserById(userId);
        Comment comment=getCommentById(commentId);
        return commentHeartRepository.findByUserAndComment(user, comment)
                .orElseThrow(()-> new HeartNotFoundException("하트를 찾을 수 없습니다."));
    }
    //게시물에 하트 추가
    @Transactional
    public Long addHeartToPost(Long userId, Long postId) {
        Users user=getUserById(userId);
        Post post=getPostById(postId);

        // 이미 하트를 추가했는지 확인
        Optional<PostHeart> existingHeart = postHeartRepository.findByUserAndPost(user, post);
        if (existingHeart.isPresent()) {
            throw new HeartAlreadyExistsException("이미 이 게시글에 하트를 추가했습니다.");
        }

        // 새로운 PostHeart 생성
        // heart는 builder보다 생성자를 쓰는게 나을까?
        PostHeart heart = PostHeart.builder()
                .user(user)
                .post(post)
                .build();

        postHeartRepository.save(heart);
        return heart.getHeartId();
    }

    //게시물에 하트 삭제
    @Transactional
    public void removeHeartFromPost(Long userId, Long postId) {
        PostHeart heart = getPostHeartById(userId,postId);
        postHeartRepository.delete(heart);
    }

    //댓글에 하트 추가
    @Transactional
    public Long addHeartToComment(Long userId, Long commentId) {
        Users user=getUserById(userId);
        Comment comment=getCommentById(commentId);

        // 이미 하트를 추가했는지 확인
        Optional<CommentHeart> existingHeart = commentHeartRepository.findByUserAndComment(user, comment);
        if (existingHeart.isPresent()) {
            throw new HeartAlreadyExistsException("이미 이 댓글에 하트를 추가했습니다.");
        }

        // 새로운 CommentHeart 생성
        CommentHeart heart = CommentHeart.builder()
                .user(user)
                .comment(comment)
                .build();

        commentHeartRepository.save(heart);
        return heart.getHeartId();
    }

    // 댓글에 하트 삭제
    @Transactional
    public void removeHeartFromComment(Long userId, Long commentId) {
        CommentHeart heart = getCommentHeartById(userId,commentId);
        commentHeartRepository.delete(heart);
    }


    // 특정 게시물의 하트 수 조회
    @Transactional(readOnly = true)
    public Long countHeartsByPost(Long postId) {
        Post post = getPostById(postId);
        return postHeartRepository.countByPost(post);
    }

    // 특정 댓글의 하트 수 조회
    @Transactional(readOnly = true)
    public Long countHeartsByComment(Long commentId) {
        Comment comment = getCommentById(commentId);
        return commentHeartRepository.countByComment(comment);
    }

    // 사용자별 게시물 하트 목록 조회
    @Transactional(readOnly = true)
    public List<PostHeart> getPostHeartsByUser(Long userId) {
        Users user = getUserById(userId);
        return postHeartRepository.findAllPostHeartByUser(user);
    }

    // 사용자별 댓글 하트 목록 조회
    @Transactional(readOnly = true)
    public List<CommentHeart> getCommentHeartsByUser(Long userId) {
        Users user = getUserById(userId);
        return commentHeartRepository.findAllCommentHeartByUser(user);
    }

}
