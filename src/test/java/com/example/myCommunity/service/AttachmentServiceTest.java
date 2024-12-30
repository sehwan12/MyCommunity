package com.example.myCommunity.service;

import com.example.myCommunity.domain.*;
import com.example.myCommunity.Exception.*;
import com.example.myCommunity.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AttachmentServiceTest {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BoardRepository boardRepository;

    private Users testUser;
    private Post testPost;
    private Board testBoard;

    // 임시 디렉토리 경로를 저장할 변수
    private static String tempUploadDir;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) throws IOException {
        // JUnit 5의 @TempDir를 사용하려면 동적 프로퍼티 설정이 필요
        File tempDir = Files.createTempDirectory("upload-test").toFile();
        tempDir.deleteOnExit();
        tempUploadDir = tempDir.getAbsolutePath();
        registry.add("file.upload-dir", () -> tempUploadDir);
    }

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

        // 파일 업로드 디렉토리 초기화
        attachmentService.init();
    }

    /**
     * 파일 업로드 디렉토리 초기화 테스트
     */
    @Test
    void init_ShouldCreateUploadDirectory() {
        // given
        File dir = new File(tempUploadDir);

        // when
        boolean exists = dir.exists();

        // then
        assertTrue(exists, "업로드 디렉토리가 생성되어야 합니다.");
        assertTrue(dir.isDirectory(), "업로드 디렉토리는 디렉토리여야 합니다.");
    }

    /**
     * 첨부파일 추가 (정상) 테스트
     */
    @Test
    void addAttachmentToPost_ShouldAddAttachmentSuccessfully() throws IOException {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "dummy image content".getBytes()
        );

        // when
        Attachment savedAttachment = attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile);

        // then
        assertNotNull(savedAttachment.getAttachmentId(), "첨부파일 ID는 null이 아니어야 합니다.");
        assertEquals(testPost.getPostId(), savedAttachment.getPost().getPostId(), "첨부파일의 게시글 ID가 일치해야 합니다.");
        assertEquals(mockFile.getSize(), savedAttachment.getAttachSize(), "첨부파일 크기가 일치해야 합니다.");
        assertTrue(savedAttachment.getAttachUrl().contains(".png"), "첨부파일 URL에 확장자가 포함되어야 합니다.");

        // 파일이 실제로 저장되었는지 확인
        File savedFile = new File(savedAttachment.getAttachUrl());
        assertTrue(savedFile.exists(), "파일이 실제로 저장되어야 합니다.");
    }

    /**
     * 첨부파일 추가 시 게시글 작성자가 아닐 경우 예외 발생 테스트
     */
    @Test
    void addAttachmentToPost_WithUnauthorizedUser_ShouldThrowException() throws IOException {
        // given
        // 다른 사용자 생성
        Users anotherUser = Users.builder()
                .username("anotherUser")
                .userEmail("anotheruser@example.com")
                .build();
        userRepository.save(anotherUser);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "dummy image content".getBytes()
        );

        // when & then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            attachmentService.addAttachmentToPost(testPost.getPostId(), anotherUser.getUserId(), mockFile);
        });

        assertEquals("첨부파일을 추가할 권한이 없습니다.", exception.getMessage());

        // 파일이 저장되지 않았는지 확인
        List<Attachment> attachments = attachmentRepository.findByPost(testPost);
        assertTrue(attachments.isEmpty(), "첨부파일이 저장되지 않아야 합니다.");
    }

    /**
     * 첨부파일 추가 시 파일이 비어있을 경우 예외 발생 테스트
     */
    @Test
    void addAttachmentToPost_WithEmptyFile_ShouldThrowException() {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "empty-image.png",
                "image/png",
                new byte[0] // 비어있는 파일
        );

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile);
        });

        assertEquals("파일이 비어있습니다.", exception.getMessage());

        // 파일이 저장되지 않았는지 확인
        List<Attachment> attachments = attachmentRepository.findByPost(testPost);
        assertTrue(attachments.isEmpty(), "첨부파일이 저장되지 않아야 합니다.");
    }

    /**
     * 첨부파일 추가 시 비허용된 MIME 타입일 경우 예외 발생 테스트
     */
    @Test
    void addAttachmentToPost_WithInvalidMimeType_ShouldThrowException() {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "dummy pdf content".getBytes()
        );

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile);
        });

        assertEquals("이미지 파일만 업로드할 수 있습니다.", exception.getMessage());

        // 파일이 저장되지 않았는지 확인
        List<Attachment> attachments = attachmentRepository.findByPost(testPost);
        assertTrue(attachments.isEmpty(), "첨부파일이 저장되지 않아야 합니다.");
    }

    /**
     * 첨부파일 추가 시 파일 크기가 제한을 초과할 경우 예외 발생 테스트
     */
    @Test
    void addAttachmentToPost_WithExceedingFileSize_ShouldThrowException() {
        // given
        // 6MB 크기의 파일 생성 (5MB 초과)
        byte[] largeFileContent = new byte[6 * 1024 * 1024];
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "large-image.png",
                "image/png",
                largeFileContent
        );

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile);
        });

        assertEquals("파일 크기는 5MB 이하이어야 합니다.", exception.getMessage());

        // 파일이 저장되지 않았는지 확인
        List<Attachment> attachments = attachmentRepository.findByPost(testPost);
        assertTrue(attachments.isEmpty(), "첨부파일이 저장되지 않아야 합니다.");
    }

    /**
     * 첨부파일 추가 시 게시글이 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void addAttachmentToPost_WithNonExistentPost_ShouldThrowException() throws IOException {
        // given
        Long nonExistentPostId = 999L;
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "dummy image content".getBytes()
        );

        // when & then
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> {
            attachmentService.addAttachmentToPost(nonExistentPostId, testUser.getUserId(), mockFile);
        });

        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());

        // 파일이 저장되지 않았는지 확인
        List<Attachment> attachments = attachmentRepository.findAll();
        assertTrue(attachments.isEmpty(), "첨부파일이 저장되지 않아야 합니다.");
    }

    /**
     * 첨부파일 삭제 (정상) 테스트
     */
    @Test
    void deleteAttachment_ShouldDeleteAttachmentSuccessfully() throws IOException {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "dummy image content".getBytes()
        );

        Attachment savedAttachment = attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile);
        Long attachmentId = savedAttachment.getAttachmentId();

        File savedFile = new File(savedAttachment.getAttachUrl());
        assertTrue(savedFile.exists(), "파일이 실제로 저장되어야 합니다.");

        // when
        attachmentService.deleteAttachment(attachmentId, testUser.getUserId());

        // then
        // 첨부파일이 데이터베이스에서 삭제되었는지 확인
        assertFalse(attachmentRepository.findById(attachmentId).isPresent(), "첨부파일이 데이터베이스에서 삭제되어야 합니다.");

        // 파일이 파일 시스템에서 삭제되었는지 확인
        assertFalse(savedFile.exists(), "파일이 파일 시스템에서 삭제되어야 합니다.");
    }

    /**
     * 첨부파일 삭제 시 게시글 작성자가 아닐 경우 예외 발생 테스트
     */
    @Test
    void deleteAttachment_WithUnauthorizedUser_ShouldThrowException() throws IOException {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "dummy image content".getBytes()
        );

        Attachment savedAttachment = attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile);
        Long attachmentId = savedAttachment.getAttachmentId();

        File savedFile = new File(savedAttachment.getAttachUrl());
        assertTrue(savedFile.exists(), "파일이 실제로 저장되어야 합니다.");

        // 다른 사용자 생성
        Users anotherUser = Users.builder()
                .username("anotherUser")
                .userEmail("anotheruser@example.com")
                .build();
        userRepository.save(anotherUser);

        // when & then
        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            attachmentService.deleteAttachment(attachmentId, anotherUser.getUserId());
        });

        assertEquals("첨부파일을 삭제할 권한이 없습니다.", exception.getMessage());

        // 첨부파일이 데이터베이스에 여전히 존재하는지 확인
        assertTrue(attachmentRepository.findById(attachmentId).isPresent(), "첨부파일이 데이터베이스에 여전히 존재해야 합니다.");

        // 파일이 파일 시스템에 여전히 존재하는지 확인
        assertTrue(savedFile.exists(), "파일이 파일 시스템에 여전히 존재해야 합니다.");
    }

    /**
     * 첨부파일 삭제 시 첨부파일이 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void deleteAttachment_WithNonExistentAttachment_ShouldThrowException() {
        // given
        Long nonExistentAttachmentId = 999L;

        // when & then
        AttachmentNotFoundException exception = assertThrows(AttachmentNotFoundException.class, () -> {
            attachmentService.deleteAttachment(nonExistentAttachmentId, testUser.getUserId());
        });

        assertEquals("첨부파일을 찾을 수 없습니다.", exception.getMessage());
    }

    /**
     * 특정 게시글의 모든 첨부파일 목록 조회 테스트 (첨부파일이 있는 경우)
     */
    @Test
    void getAttachmentsByPostId_WithAttachments_ShouldReturnAttachments() throws IOException {
        // given
        MockMultipartFile mockFile1 = new MockMultipartFile(
                "file",
                "test-image1.png",
                "image/png",
                "dummy image content 1".getBytes()
        );

        MockMultipartFile mockFile2 = new MockMultipartFile(
                "file",
                "test-image2.jpg",
                "image/jpeg",
                "dummy image content 2".getBytes()
        );

        Attachment attachment1 = attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile1);
        Attachment attachment2 = attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile2);

        // when
        List<Attachment> attachments = attachmentService.getAttachmentsByPostId(testPost.getPostId());

        // then
        assertEquals(2, attachments.size(), "첨부파일 목록의 크기가 일치해야 합니다.");
        assertTrue(attachments.stream().anyMatch(a -> a.getAttachmentId().equals(attachment1.getAttachmentId())));
        assertTrue(attachments.stream().anyMatch(a -> a.getAttachmentId().equals(attachment2.getAttachmentId())));
    }

    /**
     * 특정 게시글의 모든 첨부파일 목록 조회 테스트 (첨부파일이 없는 경우)
     */
    @Test
    void getAttachmentsByPostId_WithNoAttachments_ShouldReturnEmptyList() {
        // given
        // 새로운 게시물 생성
        Post newPost = Post.builder()
                .title("첨부파일 없는 게시물")
                .postText("내용 없음")
                .user(testUser)
                .board(testBoard)
                .build();
        postRepository.save(newPost);

        // when
        List<Attachment> attachments = attachmentService.getAttachmentsByPostId(newPost.getPostId());

        // then
        assertNotNull(attachments, "첨부파일 목록은 null이 아니어야 합니다.");
        assertTrue(attachments.isEmpty(), "첨부파일 목록이 비어 있어야 합니다.");
    }

    /**
     * 특정 게시글의 모든 첨부파일 목록 조회 시 게시글이 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void getAttachmentsByPostId_WithNonExistentPost_ShouldThrowException() {
        // given
        Long nonExistentPostId = 999L;

        // when & then
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> {
            attachmentService.getAttachmentsByPostId(nonExistentPostId);
        });

        assertEquals("게시글을 찾을 수 없습니다.", exception.getMessage());
    }

    /**
     * 첨부파일 조회 테스트 (정상)
     */
    @Test
    void getAttachmentById_ShouldReturnAttachment() throws IOException {
        // given
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "dummy image content".getBytes()
        );

        Attachment savedAttachment = attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile);
        Long attachmentId = savedAttachment.getAttachmentId();

        // when
        Attachment retrievedAttachment = attachmentService.getAttachmentById(attachmentId);

        // then
        assertNotNull(retrievedAttachment, "첨부파일이 조회되어야 합니다.");
        assertEquals(savedAttachment.getAttachmentId(), retrievedAttachment.getAttachmentId(), "첨부파일 ID가 일치해야 합니다.");
    }

    /**
     * 첨부파일 조회 시 첨부파일이 존재하지 않을 경우 예외 발생 테스트
     */
    @Test
    void getAttachmentById_WithNonExistentAttachment_ShouldThrowException() {
        // given
        Long nonExistentAttachmentId = 999L;

        // when & then
        AttachmentNotFoundException exception = assertThrows(AttachmentNotFoundException.class, () -> {
            attachmentService.getAttachmentById(nonExistentAttachmentId);
        });

        assertEquals("첨부파일을 찾을 수 없습니다.", exception.getMessage());
    }

    /**
     * 첨부파일 추가 후 파일 시스템 정리
     */
    @Test
    void cleanup_ShouldRemoveAllFiles() throws IOException {
        // given
        MockMultipartFile mockFile1 = new MockMultipartFile(
                "file",
                "test-image1.png",
                "image/png",
                "dummy image content 1".getBytes()
        );

        MockMultipartFile mockFile2 = new MockMultipartFile(
                "file",
                "test-image2.jpg",
                "image/jpeg",
                "dummy image content 2".getBytes()
        );

        Attachment attachment1 = attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile1);
        Attachment attachment2 = attachmentService.addAttachmentToPost(testPost.getPostId(), testUser.getUserId(), mockFile2);

        File file1 = new File(attachment1.getAttachUrl());
        File file2 = new File(attachment2.getAttachUrl());

        assertTrue(file1.exists(), "파일1이 실제로 저장되어야 합니다.");
        assertTrue(file2.exists(), "파일2가 실제로 저장되어야 합니다.");

        // when
        attachmentService.deleteAttachment(attachment1.getAttachmentId(), testUser.getUserId());
        attachmentService.deleteAttachment(attachment2.getAttachmentId(), testUser.getUserId());

        // then
        assertFalse(file1.exists(), "파일1이 삭제되어야 합니다.");
        assertFalse(file2.exists(), "파일2가 삭제되어야 합니다.");
    }
}
