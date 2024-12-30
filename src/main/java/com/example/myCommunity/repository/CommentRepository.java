package com.example.myCommunity.repository;

import com.example.myCommunity.domain.Comment;
import com.example.myCommunity.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostAndParentIsNull(Post post);

    List<Comment> findByParent(Comment parentComment);
}
