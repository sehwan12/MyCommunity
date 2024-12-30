package com.example.myCommunity.service;

import com.example.myCommunity.Exception.PostNotFoundException;
import com.example.myCommunity.Exception.UnauthorizedException;
import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.Board;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.Users;
import com.example.myCommunity.dto.postDTO.PostEditDTO;
import com.example.myCommunity.dto.postDTO.PostRegistrationDTO;
import com.example.myCommunity.repository.BoardRepository;
import com.example.myCommunity.repository.PostRepository;
import com.example.myCommunity.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    private Users testUser;
    private Board testBoard;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = Users.builder()
                .username("testUser")
                .userEmail("testUser@gmail.com")
                .build();
        userRepository.save(testUser);

        // 테스트 게시판 생성
        testBoard = Board.builder()
                .boardName("공지사항")
                .build();
        boardRepository.save(testBoard);
    }

    //기본 포스트
    public PostRegistrationDTO getPostRegistrationDTO() {
        return PostRegistrationDTO.builder().
                userId(testUser.getUserId()).
                boardName(testBoard.getBoardName()).
                title("원래 제목").
                postText("원래 내용").
                build();
    }

    //업데이트할 포스트
    public PostEditDTO getPostEditDTO(Long postId) {
        return PostEditDTO.builder().
                postId(postId).
                title("수정된 제목").
                postText("수정된 내용").
                build();
    }

    /**
     * 게시물 추가 테스트
     */
    @Test
    void 게시물추가_성공() {
        // given
        PostRegistrationDTO requestDTO = getPostRegistrationDTO();

        // when
        Long savedPostId = postService.addPost(requestDTO);
        Post savedPost= postRepository.findById(savedPostId).get();
        // then
        assertNotNull(savedPost.getPostId());
        assertEquals("원래 제목", savedPost.getTitle());
        assertEquals("원래 내용", savedPost.getPostText());
        assertEquals(testUser.getUserId(), savedPost.getUser().getUserId());
        assertEquals(testBoard.getBoardId(), savedPost.getBoard().getBoardId());
    }

    /**
     * 게시물 업데이트 테스트
     */
    @Test
    void 게시물업데이트_성공() {
        // given
        // 먼저 게시물을 생성
        PostRegistrationDTO createDTO = getPostRegistrationDTO();
        Long userId= createDTO.getUserId();
        Long postId = postService.addPost(createDTO);

        // 업데이트할 데이터
        PostEditDTO updateDTO = getPostEditDTO(postId);

        // when
        postService.updatePost(updateDTO,userId);

        // then
        Post post= postRepository.findById(postId).get();
        assertEquals("수정된 제목", post.getTitle());
        assertEquals("수정된 내용", post.getPostText());
    }

    /**
     * 게시물 업데이트 시 다른 사용자가 시도할 경우 예외 발생 테스트
     */
    @Test
    void 게시물업데이트예외_다른이용자시도() {
        // given
        // 먼저 게시물을 생성
        PostRegistrationDTO createDTO = getPostRegistrationDTO();
        Long postId = postService.addPost(createDTO);

        // 다른 사용자 생성
        Users anotherUser = Users.builder()
                .username("anotherUser")
                .userEmail("testUser2@gmail.com")
                .build();
        userRepository.save(anotherUser);

        // 업데이트할 데이터
        PostEditDTO updateDTO = getPostEditDTO(postId);

        // when & then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            postService.updatePost(updateDTO, anotherUser.getUserId());
        });

        assertEquals("게시글을 삭제할 권한이 없습니다.", exception.getMessage());
    }

    /**
     * 게시물 삭제 테스트
     */
    @Test
    void 게시물삭제_성공() {
        // given
        // 먼저 게시물을 생성
        PostRegistrationDTO createDTO = getPostRegistrationDTO();
        Long postId = postService.addPost(createDTO);

        // when
        postService.deletePost(postId, testUser.getUserId());

        // then
        assertFalse(postRepository.findById(postId).isPresent());
    }

    /**
     * 게시물 삭제 시 다른 사용자가 시도할 경우 예외 발생 테스트
     */
    @Test
    void 게시물삭제예외_다른이용자시도() {
        // given
        // 먼저 게시물을 생성
        PostRegistrationDTO createDTO = getPostRegistrationDTO();
        Long postId = postService.addPost(createDTO);

        // 다른 사용자 생성
        Users anotherUser = Users.builder()
                .username("anotherUser")
                .userEmail("testUser@gmail.com")
                .build();
        userRepository.save(anotherUser);

        // when & then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            postService.deletePost(postId, anotherUser.getUserId());
        });

        assertEquals("게시글을 삭제할 권한이 없습니다.", exception.getMessage());
    }

    /**
     * 특정 게시판의 게시물 전부 조회 테스트
     */
    @Test
    void 게시물조회_게시판별() {
        // given
        // 다른 게시판 생성
        Board anotherBoard = Board.builder()
                .boardName("자유게시판")
                .build();
        boardRepository.save(anotherBoard);

        // 게시물 생성
        PostRegistrationDTO dto1 = getPostRegistrationDTO();
        Long postId1 = postService.addPost(dto1);

        PostRegistrationDTO dto2 = getPostRegistrationDTO();
        Long postId2 = postService.addPost(dto2);

        PostRegistrationDTO dto3 = getPostRegistrationDTO();
        dto3.setBoardName(anotherBoard.getBoardName());
        Long postId3 = postService.addPost(dto3);

        // when
        List<Post> postsByBoard1 = postService.getAllPostsByBoard(testBoard.getBoardId());

        // then
        assertEquals(2, postsByBoard1.size());
        assertTrue(postsByBoard1.stream().anyMatch(p -> p.getPostId().equals(postId1)));
        assertTrue(postsByBoard1.stream().anyMatch(p -> p.getPostId().equals(postId2)));
    }

    /**
     * 특정 사용자의 게시물 전부 조회 테스트
     */
    @Test
    void 게시물조회_사용자별() {
        // given
        // 다른 사용자 생성
        Users anotherUser = Users.builder()
                .username("anotherUser")
                .userEmail("testUser2@gmail.com")
                .build();
        userRepository.save(anotherUser);

        // 게시물 생성
        PostRegistrationDTO dto1 = getPostRegistrationDTO();
        Long postId1 = postService.addPost(dto1);

        PostRegistrationDTO dto2 =getPostRegistrationDTO();
        Long postId2 = postService.addPost(dto2);

        PostRegistrationDTO dto3 =getPostRegistrationDTO();
        dto3.setUserId(anotherUser.getUserId());
        Long postId3 = postService.addPost(dto3);

        // when
        List<Post> postsByUser1 = postService.getAllPostsByUser(testUser.getUserId());

        // then
        assertEquals(2, postsByUser1.size());
        assertTrue(postsByUser1.stream().anyMatch(p -> p.getPostId().equals(postId1)));
        assertTrue(postsByUser1.stream().anyMatch(p -> p.getPostId().equals(postId2)));
    }

    /**
     * 게시물 업데이트 시 게시글이 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 게시물업데이트예외_게시물이없는경우() {
        // given
        Long nonExistentPostId = 999L;
        PostEditDTO updateDTO = getPostEditDTO(nonExistentPostId);

        // when & then
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> {
            postService.updatePost(updateDTO, testUser.getUserId());
        });

        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
    }

    /**
     * 게시물 삭제 시 게시글이 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 게시물삭제예외_게시글존재x() {
        // given
        Long nonExistentPostId = 999L;

        // when & then
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> {
            postService.deletePost(nonExistentPostId, testUser.getUserId());
        });

        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
    }

    /**
     * 게시물 생성 시 사용자가 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 게시물생성예외_사용자존재x() {
        // given
        Long nonExistentUserId = 999L;
        PostRegistrationDTO requestDTO = getPostRegistrationDTO();
        requestDTO.setUserId(nonExistentUserId);

        // when & then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            postService.addPost(requestDTO);
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    }

}