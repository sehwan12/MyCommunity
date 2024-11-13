package com.example.myCommunity.repository;

import com.example.myCommunity.domain.Attachment;
import com.example.myCommunity.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment,Long> {
    List<Attachment> findByPost(Post post);
}
