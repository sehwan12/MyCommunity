package com.example.myCommunity.service;

import com.example.myCommunity.domain.User;
import com.example.myCommunity.dto.postDTO.PostRegistrationDTO;
import com.example.myCommunity.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class PostServiceTest {
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepo;

    @Test
    void 게시글추가_성공() {

    }

    @Test
    void updatePost() {
    }

    @Test
    void deletePost() {
    }

    @Test
    void getAllPostsByBoard() {
    }

    @Test
    void getAllPostsByUser() {
    }
}