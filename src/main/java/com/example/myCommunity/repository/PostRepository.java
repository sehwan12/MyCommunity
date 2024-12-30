package com.example.myCommunity.repository;

import com.example.myCommunity.domain.Board;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    //게시판의 모든 게시글 조회
     List<Post> findByBoard(Board board);
     //유저의 모든 게시글 조회
     List<Post> findByUser(Users user);

     Page<Post> findByBoardOrderByCreatedDateDesc(Board board, Pageable pageable);
}
