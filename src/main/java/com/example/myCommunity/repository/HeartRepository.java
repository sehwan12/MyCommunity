package com.example.myCommunity.repository;

import com.example.myCommunity.domain.Comment;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.User;
import com.example.myCommunity.domain.heart.CommentHeart;
import com.example.myCommunity.domain.heart.Heart;
import com.example.myCommunity.domain.heart.PostHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HeartRepository extends JpaRepository<Heart,Long> {
    // 특정 사용자와 특정 엔티티(PostHeart)에 대한 하트 조회
    Optional<Heart> findByUserAndPost(Long userId, Post post);

    // 특정 사용자와 특정 엔티티(CommentHeart)에 대한 하트 조회
    Optional<Heart> findByUserAndComment(Long userId, Comment comment);

    // 특정 엔티티(PostHeart)에 대한 모든 하트 조회
    List<Heart> findByPost(Post post);

    // 특정 엔티티(CommentHeart)에 대한 모든 하트 조회
    List<Heart> findByComment(Comment comment);

    // 특정 엔티티(PostHeart)에 대한 하트 수 조회
    Long countByPost(Post post);

    // 특정 엔티티(CommentHeart)에 대한 하트 수 조회
    Long countByComment(Comment comment);

    List<Heart> findAllByUser(User user);

    List<PostHeart> findAllByUserAndDiscriminator(User user, String post);

    @Query("SELECT h FROM PostHeart h WHERE h.user = :user AND TYPE(h) = :dtype")
    List<PostHeart> findAllPostHeartByUser(@Param("user") User user);


    @Query("SELECT h FROM CommentHeart h WHERE h.user = :user AND TYPE(h) = :dtype")
    List<CommentHeart> findAllCommentHeartByUser(@Param("user") User user);
}

