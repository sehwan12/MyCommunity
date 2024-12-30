package com.example.myCommunity.repository;

import com.example.myCommunity.domain.Comment;
import com.example.myCommunity.domain.Users;
import com.example.myCommunity.domain.heart.CommentHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CommentHeartRepository extends JpaRepository<CommentHeart,Long> {
    // 특정 사용자와 특정 엔티티(CommentHeart)에 대한 하트 조회
    Optional<CommentHeart> findByUserAndComment(Users user, Comment comment);
    // 특정 엔티티(CommentHeart)에 대한 모든 하트 조회
    List<CommentHeart> findByComment(Comment comment);

    // 특정 엔티티(CommentHeart)에 대한 하트 수 조회
    Long countByComment(Comment comment);

    List<CommentHeart> findAllCommentHeartByUser(Users user);
}
