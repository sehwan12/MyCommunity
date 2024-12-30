package com.example.myCommunity.repository;

import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.Users;
import com.example.myCommunity.domain.heart.PostHeart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostHeartRepository extends JpaRepository<PostHeart,Long> {
    // 특정 사용자와 특정 엔티티(PostHeart)에 대한 하트 조회
    Optional<PostHeart> findByUserAndPost(Users user, Post post);



    // 특정 엔티티(PostHeart)에 대한 모든 하트 조회
    List<PostHeart> findByPost(Post post);


    // 특정 엔티티(PostHeart)에 대한 하트 수 조회
    Long countByPost(Post post);

    List<PostHeart> findAllPostHeartByUser(Users user);


}

