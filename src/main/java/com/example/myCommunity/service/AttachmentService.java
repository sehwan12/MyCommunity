package com.example.myCommunity.service;

import com.example.myCommunity.Exception.AttachmentNotFoundException;
import com.example.myCommunity.Exception.PostNotFoundException;
import com.example.myCommunity.Exception.UnauthorizedException;
import com.example.myCommunity.domain.Attachment;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.repository.AttachmentRepository;
import com.example.myCommunity.repository.PostRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AttachmentService{
    private final AttachmentRepository attachmentRepository;
    private final PostRepository postRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 파일 업로드 디렉토리 초기화
     */
    //@Transactional
    public void init() {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 첨부파일 추가 (단일 파일)
     */
    //메소드 추상화 필요
    @Transactional
    public Attachment addAttachmentToPost(Long postId, Long currentUserId, MultipartFile file) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));

        //User currentUser = getCurrentUser();
        // 게시글 작성자만 삭제할 수 있도록 검증
        if (!post.getUser().getUserId().equals(currentUserId)) {
            throw new UnauthorizedException("첨부파일을 추가할 권한이 없습니다.");
        }

        // 파일 유효성 검증
        validateFile(file);

        // 파일 저장
        String filename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(filename);
        String uniqueFilename = UUID.randomUUID().toString() + "." + extension;
        String filepath = uploadDir + File.separator + uniqueFilename;

        try {
            file.transferTo(new File(filepath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }

        // Attachment 엔티티 생성 및 저장
        Attachment attachment = Attachment.builder()
                .post(post)
                .attachSize(file.getSize())
                .attachUrl(filepath)
                .build();

        return attachmentRepository.save(attachment);
    }

    /**
     * 첨부파일 삭제
     */
    @Transactional
    public void deleteAttachment(Long attachmentId, Long currentUserId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("첨부파일을 찾을 수 없습니다."));

        Post post = attachment.getPost();

        //User currentUser = getCurrentUser();

        // 게시글 작성자만 삭제할 수 있도록 검증
        if (!post.getUser().getUserId().equals(currentUserId)) {
            throw new UnauthorizedException("첨부파일을 삭제할 권한이 없습니다.");
        }

        // 파일 시스템에서 파일 삭제
        File file = new File(attachment.getAttachUrl());
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                throw new RuntimeException("파일 삭제에 실패했습니다.");
            }
        }

        // 데이터베이스에서 첨부파일 삭제
        attachmentRepository.delete(attachment);
    }

    /**
     * 특정 게시글의 모든 첨부파일 목록 조회
     */
    @Transactional
    public List<Attachment> getAttachmentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다."));
        return attachmentRepository.findByPost(post);
    }

    /**
     * 첨부파일 조회
     */
    @Transactional
    public Attachment getAttachmentById(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException("첨부파일을 찾을 수 없습니다."));
        return attachment;
    }

    /**
     * 파일 유효성 검증 메소드
     */
    private void validateFile(MultipartFile file) {
        // MIME 타입 검증 (예: 이미지 파일만 허용)
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        // 파일 크기 제한 (예: 5MB)
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB
            throw new IllegalArgumentException("파일 크기는 5MB 이하이어야 합니다.");
        }
    }

}


