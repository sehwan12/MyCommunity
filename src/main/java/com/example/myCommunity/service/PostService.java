package com.example.myCommunity.service;

import com.example.myCommunity.Exception.BoardNotFoundException;
import com.example.myCommunity.Exception.PostNotFoundException;
import com.example.myCommunity.Exception.UnauthorizedException;
import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.Board;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.Users;
import com.example.myCommunity.dto.postDTO.PostEditDTO;
import com.example.myCommunity.dto.postDTO.PostRegistrationDTO;
import com.example.myCommunity.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    private Users getCurrentUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public Post getCurrentPost(Long postId) {
        Post post=postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        return post;
    }
    private void validateUser(Post post, Long currentUserId) {
        if (!post.isAuthor(currentUserId)) {
            throw new UnauthorizedException("게시글을 삭제할 권한이 없습니다.");
        }
    }

    //게시글 추가
    @Transactional
    public Long addPost(PostRegistrationDTO request){
        Users user= getCurrentUser(request.getUserId());
        Board board=boardService.getBoardByName(request.getBoardName());
        Post post= Post.builder()
                .board(board)
                .title(request.getTitle())
                .postText(request.getPostText())
                .user(user)
        .build();
        postRepository.save(post);
        return post.getPostId();
    }

    @Transactional
    public void updatePost(PostEditDTO edit, Long userId){
        Post post=getCurrentPost(edit.getPostId());
        validateUser(post, userId);
        post.updatePost(edit);
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

    /**
     * 특정 게시판에 속한 게시글을 페이지 단위로 가져옵니다.
     *
     * @param board    게시판 엔티티
     * @param pageable 페이지 요청 정보
     * @return 게시글 페이지
     */
    public Page<Post> getPostsByBoard(Board board, Pageable pageable) {
        return postRepository.findByBoardOrderByCreatedDateDesc(board, pageable);
    }

    //특정 유저 게시글 전부조회
    @Transactional(readOnly = true)
    public List<Post> getAllPostsByUser(Long userId) {
        Users user=getCurrentUser(userId);
        return postRepository.findByUser(user);
    }
    /**
     * 특정 게시판에서 최신 글을 제한된 개수만큼 가져오는 메소드
     *
     * @param board 게시판 엔티티
     * @param limit 가져올 글의 최대 개수
     * @return      최신글 목록
     */
    public List<Post> getRecentPostsByBoard(Board board, int limit){
        return postRepository.findByBoardOrderByCreatedDateDesc(board, PageRequest.of(0, limit))
                .getContent();
    }

}
