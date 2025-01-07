package com.example.myCommunity.Controller;

// AttachmentController.java
import com.example.myCommunity.Exception.AttachmentNotFoundException;
import com.example.myCommunity.Exception.UnauthorizedException;
import com.example.myCommunity.domain.Attachment;
import com.example.myCommunity.service.AttachmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.*;

@Controller
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    // 첨부파일 다운로드
    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long attachmentId) {
        try {
            Attachment attachment = attachmentService.getAttachmentById(attachmentId);
            Path path = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 첨부파일 삭제
    @PostMapping("/delete/{attachmentId}")
    public String deleteAttachment(@PathVariable Long attachmentId,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        try {
            Long currentUserId = (Long) session.getAttribute("userId");
            if (currentUserId == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
                return "redirect:/users/login";
            }

            Attachment attachment = attachmentService.getAttachmentById(attachmentId);
            //boardname과 Postid를 attachment에서 가져오는 방식이므로 attachment를 삭제시키기 전에 추출시켜 놓자.
            String boardName= attachment.getPost().getBoard().getBoardName();
            Long postId = attachment.getPost().getPostId();
            attachmentService.deleteAttachment(attachmentId, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage", "첨부파일이 삭제되었습니다.");
            return "redirect:/" +boardName +"/editPost/"+ postId;
        } catch (AttachmentNotFoundException | UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "첨부파일 삭제 중 오류가 발생했습니다.");
            return "redirect:/";
        }
    }
}
