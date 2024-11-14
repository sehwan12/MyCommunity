package com.example.myCommunity.service;

import com.example.myCommunity.Exception.BoardNotFoundException;
import com.example.myCommunity.Exception.PostNotFoundException;
import com.example.myCommunity.Exception.UnauthorizedException;
import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.Board;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.User;
import com.example.myCommunity.dto.postDTO.PostEditDTO;
import com.example.myCommunity.dto.postDTO.PostRegistrationDTO;
import com.example.myCommunity.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    private User getCurrentUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private Post getCurrentPost(Long postId) {
        Post post=postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        return post;
    }
    private void validateUser(Post post, Long currentUserId) {
        if (!post.getUser().getUserId().equals(currentUserId)) {
            throw new UnauthorizedException("게시글을 삭제할 권한이 없습니다.");
        }
    }

    //게시글 추가
    @Transactional
    public Post addPost(PostRegistrationDTO request){
        User user= getCurrentUser(request.getUserId());
        Post post= Post.builder()
                .board(request.getBoard())
                .title(request.getTitle())
                .postText(request.getPostText())
                .user(user)
        .build();
        return postRepository.save(post);
    }

    @Transactional
    public Post updatePost(PostEditDTO edit){
        Post post=getCurrentPost(edit.getPostId());
        post.updatePost(edit.getTitle(), edit.getPostText());
        validateUser(post, edit.getUserId());
        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId, Long currentUserId) {
        Post post=getCurrentPost(postId);
        // 작성자 ID와 현재 사용자 ID를 직접 비교
        validateUser(post, currentUserId);
        postRepository.delete(post);
    }

    //특정 게시판 게시글 전부조회
    @Transactional(readOnly = true)
    public List<Post> getAllPostsByBoard(Long boardId) {
        Board board=boardRepository.findById(boardId)
                .orElseThrow(()-> new BoardNotFoundException("존재하지 않는 게시판입니다."));
        return postRepository.findByBoard(board);
    }

    //특정 유저 게시글 전부조회
    @Transactional(readOnly = true)
    public List<Post> getAllPostsByUser(Long userId) {
        User user=getCurrentUser(userId);
        return postRepository.findByUser(user);
    }

}
