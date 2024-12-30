package com.example.myCommunity.service;


import com.example.myCommunity.domain.*;
import com.example.myCommunity.Exception.*;
import com.example.myCommunity.domain.heart.CommentHeart;
import com.example.myCommunity.domain.heart.PostHeart;
import com.example.myCommunity.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HeartServiceTest {

    @Autowired
    private HeartService heartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostHeartRepository postHeartRepository;

    @Autowired
    private CommentHeartRepository commentHeartRepository;

    @Autowired
    private BoardRepository boardRepository;

    private Users testUser;
    private Post testPost;
    private Comment testComment;
    private Board testBoard;

    @BeforeEach
    void setUp() {
        // 테스트 게시판 생성
        testBoard = Board.builder()
                .boardName("공지사항")
                .build();
        boardRepository.save(testBoard);

        // 테스트 사용자 생성
        testUser = Users.builder()
                .username("testUser")
                .userEmail("testuser@example.com") // 이메일 설정
                .build();
        userRepository.save(testUser);

        // 테스트 게시물 생성
        testPost = Post.builder()
                .title("테스트 게시물")
                .postText("테스트 내용")
                .user(testUser)
                .board(testBoard)
                .build();
        postRepository.save(testPost);

        // 테스트 댓글 생성
        testComment = Comment.builder()
                .commentText("테스트 댓글")
                .user(testUser)
                .post(testPost)
                .build();
        commentRepository.save(testComment);
    }

    /**
     * 게시물에 하트 추가 테스트
     */
    @Test
    void 게시물하트_추가_성공() {
        // when
        Long postHeartId = heartService.addHeartToPost(testUser.getUserId(), testPost.getPostId());
        PostHeart postHeart = postHeartRepository.findById(postHeartId)
                .orElseThrow(()-> new HeartNotFoundException("하트를 찾을 수 없습니다."));
        // then
        assertNotNull(postHeartId);
        assertEquals(testUser.getUserId(), postHeart.getUser().getUserId());
        assertEquals(testPost.getPostId(), postHeart.getPost().getPostId());

        // 하트 개수 확인
        Long heartCount = heartService.countHeartsByPost(testPost.getPostId());
        assertEquals(1L, heartCount);
    }

    /**
     * 게시물에 이미 하트를 추가했을 경우 예외 발생 테스트
     */
    @Test
    void 게시물하트추가예외_하트가이미있는경우() {
        // given
        heartService.addHeartToPost(testUser.getUserId(), testPost.getPostId());

        // when & then
        HeartAlreadyExistsException exception = assertThrows(HeartAlreadyExistsException.class, () -> {
            heartService.addHeartToPost(testUser.getUserId(), testPost.getPostId());
        });

        assertEquals("이미 이 게시글에 하트를 추가했습니다.", exception.getMessage());

        // 하트 개수 확인
        Long heartCount = heartService.countHeartsByPost(testPost.getPostId());
        assertEquals(1L, heartCount);
    }

    /**
     * 게시물에 하트 추가 시 사용자 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 게시물하트추가예외_사용자존재x() {
        // given
        Long nonExistentUserId = 999L;

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            heartService.addHeartToPost(nonExistentUserId, testPost.getPostId());
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());

        // 하트 개수 확인
        Long heartCount = heartService.countHeartsByPost(testPost.getPostId());
        assertEquals(0L, heartCount);
    }

    /**
     * 게시물에 하트 추가 시 게시물 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 게시물하트추가예외_() {
        // given
        Long nonExistentPostId = 999L;

        // when & then
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> {
            heartService.addHeartToPost(testUser.getUserId(), nonExistentPostId);
        });

        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());

        // 하트 개수 확인
        Long heartCount = heartService.countHeartsByPost(testPost.getPostId());
        assertEquals(0L, heartCount);
    }

    /**
     * 게시물에 하트 삭제 테스트
     */
    @Test
    void 게시물하트삭제_성공() {
        // given
        Long postHeartId = heartService.addHeartToPost(testUser.getUserId(), testPost.getPostId());
        PostHeart postHeart = postHeartRepository.findById(postHeartId)
                .orElseThrow(()-> new HeartNotFoundException("하트를 찾을 수 없습니다."));

        Long heartCountBefore = heartService.countHeartsByPost(testPost.getPostId());
        assertEquals(1L, heartCountBefore);

        // when
        heartService.removeHeartFromPost(testUser.getUserId(), testPost.getPostId());

        // then
        //게시물하트수가 0인지 확인
        assertFalse(postRepository.findById(postHeart.getHeartId()).isPresent(), "하트가 삭제되어야 합니다.");
        Long heartCountAfter = heartService.countHeartsByPost(testPost.getPostId());
        assertEquals(0L, heartCountAfter);
    }

    /**
     * 게시물에 하트 삭제 시 하트가 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 게시물하트삭제예외_하트존재x() {
        // when & then
        HeartNotFoundException exception = assertThrows(HeartNotFoundException.class, () -> {
            heartService.removeHeartFromPost(testUser.getUserId(), testPost.getPostId());
        });

        assertEquals("하트를 찾을 수 없습니다.", exception.getMessage());
    }

    /**
     * 게시물에 하트 삭제 시 사용자 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 게시물하트삭제예외_사용자존재x() {
        // given
        heartService.addHeartToPost(testUser.getUserId(), testPost.getPostId());
        Long heartCountBefore = heartService.countHeartsByPost(testPost.getPostId());
        assertEquals(1L, heartCountBefore);

        Long nonExistentUserId = 999L;

        // when & then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            heartService.removeHeartFromPost(nonExistentUserId, testPost.getPostId());
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());

        // 하트 개수 확인
        Long heartCountAfter = heartService.countHeartsByPost(testPost.getPostId());
        assertEquals(1L, heartCountAfter);
    }

    /**
     * 게시물에 하트 삭제 시 게시물 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 게시물하트삭제예외_게시물존재x() {
        // given
        Long nonExistentPostId = 999L;

        // when & then
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> {
            heartService.removeHeartFromPost(testUser.getUserId(), nonExistentPostId);
        });

        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
    }

    /**
     * 댓글에 하트 추가 테스트
     */
    @Test
    void 댓글하트추가_성공() {
        // when
        Long commentHeartId = heartService.addHeartToComment(testUser.getUserId(), testComment.getCommentId());
        CommentHeart commentHeart=heartService.getCommentHeartById(testUser.getUserId(), testComment.getCommentId());
        // then
        assertNotNull(commentHeartId);
        assertEquals(testUser.getUserId(), commentHeart.getUser().getUserId());
        assertEquals(testComment.getCommentId(), commentHeart.getComment().getCommentId());

        // 하트 개수 확인
        Long heartCount = heartService.countHeartsByComment(testComment.getCommentId());
        assertEquals(1L, heartCount);
    }

    /**
     * 댓글에 이미 하트를 추가했을 경우 예외 발생 테스트
     */
    @Test
    void 댓글하트추가예외_이미존재() {
        // given
        heartService.addHeartToComment(testUser.getUserId(), testComment.getCommentId());

        // when & then
        HeartAlreadyExistsException exception = assertThrows(HeartAlreadyExistsException.class, () -> {
            heartService.addHeartToComment(testUser.getUserId(), testComment.getCommentId());
        });

        assertEquals("이미 이 댓글에 하트를 추가했습니다.", exception.getMessage());

        // 하트 개수 확인
        Long heartCount = heartService.countHeartsByComment(testComment.getCommentId());
        assertEquals(1L, heartCount);
    }

    /**
     * 댓글에 하트 추가 시 사용자 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 댓글하트추가예외_사용자존재x() {
        // given
        Long nonExistentUserId = 999L;

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            heartService.addHeartToComment(nonExistentUserId, testComment.getCommentId());
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());

        // 하트 개수 확인
        Long heartCount = heartService.countHeartsByComment(testComment.getCommentId());
        assertEquals(0L, heartCount);
    }

    /**
     * 댓글에 하트 추가 시 댓글 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 댓글하트추가예외_댓글존재x() {
        // given
        Long nonExistentCommentId = 999L;

        // when & then
        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class, () -> {
            heartService.addHeartToComment(testUser.getUserId(), nonExistentCommentId);
        });

        assertEquals("댓글을 찾을 수 없습니다.", exception.getMessage());

        // 하트 개수 확인
        Long heartCount = heartService.countHeartsByComment(testComment.getCommentId());
        assertEquals(0L, heartCount);
    }

    /**
     * 댓글에 하트 삭제 테스트
     */
    @Test
    void 댓글하트삭제_성공() {
        // given
        heartService.addHeartToComment(testUser.getUserId(), testComment.getCommentId());
        Long heartCountBefore = heartService.countHeartsByComment(testComment.getCommentId());
        assertEquals(1L, heartCountBefore);

        // when
        heartService.removeHeartFromComment(testUser.getUserId(), testComment.getCommentId());

        // then
        Long heartCountAfter = heartService.countHeartsByComment(testComment.getCommentId());
        assertEquals(0L, heartCountAfter);
    }

    /**
     * 댓글에 하트 삭제 시 하트가 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 댓글하트삭제예외_하트존재x() {
        // when & then
        HeartNotFoundException exception = assertThrows(HeartNotFoundException.class, () -> {
            heartService.removeHeartFromComment(testUser.getUserId(), testComment.getCommentId());
        });

        assertEquals("하트를 찾을 수 없습니다.", exception.getMessage());
    }

    /**
     * 댓글에 하트 삭제 시 사용자 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 댓글하트삭제예외_사용자존재x() {
        // given
        heartService.addHeartToComment(testUser.getUserId(), testComment.getCommentId());
        Long heartCountBefore = heartService.countHeartsByComment(testComment.getCommentId());
        assertEquals(1L, heartCountBefore);

        Long nonExistentUserId = 999L;

        // when & then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            heartService.removeHeartFromComment(nonExistentUserId, testComment.getCommentId());
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());

        // 하트 개수 확인
        Long heartCountAfter = heartService.countHeartsByComment(testComment.getCommentId());
        assertEquals(1L, heartCountAfter);
    }

    /**
     * 댓글에 하트 삭제 시 댓글 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void 댓글하트삭제예외_댓글존재x() {
        // given
        Long nonExistentCommentId = 999L;

        // when & then
        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class, () -> {
            heartService.removeHeartFromComment(testUser.getUserId(), nonExistentCommentId);
        });

        assertEquals("댓글을 찾을 수 없습니다.", exception.getMessage());
    }

    /**
     * 특정 게시물의 하트 수 조회 테스트
     */
    @Test
    void 게시물하트수조회() {
        // given
        heartService.addHeartToPost(testUser.getUserId(), testPost.getPostId());
        Long heartCount = heartService.countHeartsByPost(testPost.getPostId());
        assertEquals(1L, heartCount);

        // 다른 사용자 생성
        Users anotherUser = Users.builder()
                .username("anotherUser")
                .userEmail("anotheruser@example.com")
                .build();
        userRepository.save(anotherUser);

        // 두 번째 하트 추가
        heartService.addHeartToPost(anotherUser.getUserId(), testPost.getPostId());
        Long updatedHeartCount = heartService.countHeartsByPost(testPost.getPostId());
        assertEquals(2L, updatedHeartCount);
    }

    /**
     * 특정 댓글의 하트 수 조회 테스트
     */
    @Test
    void 댓글하트수조회() {
        // given
        heartService.addHeartToComment(testUser.getUserId(), testComment.getCommentId());
        Long heartCount = heartService.countHeartsByComment(testComment.getCommentId());
        assertEquals(1L, heartCount);

        // 다른 사용자 생성
        Users anotherUser = Users.builder()
                .username("anotherUser")
                .userEmail("anotheruser@example.com")
                .build();
        userRepository.save(anotherUser);

        // 두 번째 하트 추가
        heartService.addHeartToComment(anotherUser.getUserId(), testComment.getCommentId());
        Long updatedHeartCount = heartService.countHeartsByComment(testComment.getCommentId());
        assertEquals(2L, updatedHeartCount);
    }

    /**
     * 사용자별 게시물 하트 목록 조회 테스트
     */
    @Test
    void 사용자별_게시물하트목록조회() {
        // given
        heartService.addHeartToPost(testUser.getUserId(), testPost.getPostId());

        // 다른 게시물 생성
        Post anotherPost = Post.builder()
                .title("다른 게시물")
                .postText("다른 내용")
                .user(testUser)
                .board(testBoard)
                .build();
        postRepository.save(anotherPost);

        // 두 번째 하트 추가
        heartService.addHeartToPost(testUser.getUserId(), anotherPost.getPostId());

        // when
        List<PostHeart> postHearts = heartService.getPostHeartsByUser(testUser.getUserId());

        // then
        assertEquals(2, postHearts.size());
        assertTrue(postHearts.stream().anyMatch(ph -> ph.getPost().getPostId().equals(testPost.getPostId())));
        assertTrue(postHearts.stream().anyMatch(ph -> ph.getPost().getPostId().equals(anotherPost.getPostId())));
    }

    /**
     * 사용자별 댓글 하트 목록 조회 테스트
     */
    @Test
    void 사용자별_댓글하트목록조회() {
        // given
        heartService.addHeartToComment(testUser.getUserId(), testComment.getCommentId());

        // 다른 댓글 생성
        Comment anotherComment = Comment.builder()
                .commentText("다른 댓글")
                .user(testUser)
                .post(testPost)
                .build();
        commentRepository.save(anotherComment);

        // 두 번째 하트 추가
        heartService.addHeartToComment(testUser.getUserId(), anotherComment.getCommentId());

        // when
        List<CommentHeart> commentHearts = heartService.getCommentHeartsByUser(testUser.getUserId());

        // then
        assertEquals(2, commentHearts.size());
        assertTrue(commentHearts.stream().anyMatch(ch -> ch.getComment().getCommentId().equals(testComment.getCommentId())));
        assertTrue(commentHearts.stream().anyMatch(ch -> ch.getComment().getCommentId().equals(anotherComment.getCommentId())));
    }
}
