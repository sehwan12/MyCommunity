package com.example.myCommunity.service;

import com.example.myCommunity.domain.Comment;
import com.example.myCommunity.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {
    @Autowired
    private final CommentRepository commentRepository;

    @Transactional
    public  void addComment(Comment comment){
        commentRepository.save(comment);
    }

    @Transactional
    public void modifyComment(Comment comment){

    }

}
