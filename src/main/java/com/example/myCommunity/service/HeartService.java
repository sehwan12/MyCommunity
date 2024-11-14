package com.example.myCommunity.service;

import com.example.myCommunity.Exception.*;
import com.example.myCommunity.domain.Comment;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.User;
import com.example.myCommunity.domain.heart.CommentHeart;
import com.example.myCommunity.domain.heart.Heart;
import com.example.myCommunity.domain.heart.PostHeart;
import com.example.myCommunity.repository.*;
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

    /**
     * 게시물에 하트 추가
     */
    @Transactional
    public Heart addHeartToPost(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));

        // 이미 하트를 추가했는지 확인
        Optional<PostHeart> existingHeart = postHeartRepository.findByUserAndPost(user, post);
        if (existingHeart.isPresent()) {
            throw new HeartAlreadyExistsException("이미 이 게시글에 하트를 추가했습니다.");
        }

        // 새로운 PostHeart 생성
        PostHeart heart = PostHeart.builder()
                .user(user)
                .post(post)
                .build();

        return postHeartRepository.save(heart);
    }

    /**
     * 게시물에 하트 삭제
     */
    @Transactional
    public void removeHeartFromPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        User user= userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        PostHeart heart = postHeartRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> new HeartNotFoundException("하트를 찾을 수 없습니다."));

        postHeartRepository.delete(heart);
    }

    /**
     * 댓글에 하트 추가
     */
    @Transactional
    public Heart addHeartToComment(Long userId, Long commentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));

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

        return commentHeartRepository.save(heart);
    }

    /**
     * 댓글에 하트 삭제
     */
    @Transactional
    public void removeHeartFromComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
        User user= userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        CommentHeart heart = commentHeartRepository.findByUserAndComment(user, comment)
                .orElseThrow(() -> new HeartNotFoundException("하트를 찾을 수 없습니다."));

        commentHeartRepository.delete(heart);
    }

    /**
     * 특정 게시물의 하트 수 조회
     */
    @Transactional(readOnly = true)
    public Long countHeartsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        return postHeartRepository.countByPost(post);
    }

    /**
     * 특정 댓글의 하트 수 조회
     */
    @Transactional(readOnly = true)
    public Long countHeartsByComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다."));
        return commentHeartRepository.countByComment(comment);
    }

//    /**
//     * 사용자별 하트 목록 조회
//     */
//    @Transactional(readOnly = true)
//    public List<Heart> getHeartsByUser(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//        // 하트 엔티티는 다형성을 가지므로, 필요에 따라 하위 클래스로 캐스팅할 수 있습니다.
//        return postHeartRepository.findAllByUser(user);
//    }

    /**
     * 사용자별 게시물 하트 목록 조회
     */
    @Transactional(readOnly = true)
    public List<PostHeart> getPostHeartsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        // PostHeart만 조회하기 위해 별도의 리포지토리 메소드가 필요할 수 있습니다.
        //return heartRepository.findAllByUserAndDiscriminator(user, "POST");
        return postHeartRepository.findAllPostHeartByUser(user);
    }
    //사용자별 댓글 하트 목록 조회
    @Transactional(readOnly = true)
    public List<CommentHeart> getCommentHeartsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()->new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return commentHeartRepository.findAllCommentHeartByUser(user);
    }

}
