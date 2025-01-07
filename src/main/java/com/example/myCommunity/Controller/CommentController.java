package com.example.myCommunity.Controller;

import com.example.myCommunity.Exception.PostNotFoundException;
import com.example.myCommunity.Exception.UnauthorizedException;
import com.example.myCommunity.domain.Comment;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.dto.commentDto.CommentEditDTO;
import com.example.myCommunity.dto.commentDto.CommentRequestDTO;
import com.example.myCommunity.service.CommentService;
import com.example.myCommunity.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    /**
     * 댓글 및 대댓글 추가
     */
    @PostMapping("/add")
    public String addComment(@PathVariable Long postId,
                             @Valid @ModelAttribute CommentRequestDTO commentRequestDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        if (bindingResult.hasErrors()) {
            // 오류 발생 시, 게시글 상세 페이지로 리다이렉트하면서 오류 메시지 전달
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 작성 중 오류가 발생했습니다.");
            return "redirect:/" + getBoardName(postId) + "/" + postId;
        }

        // 사용자 ID 가져오기 (세션에서)
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            // 로그인하지 않은 사용자 처리
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        // `userId`를 `CommentRequestDTO`에 설정
        commentRequestDTO.setUserId(userId);

        try {
            // 댓글 추가
            commentService.addComment(commentRequestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "댓글이 성공적으로 작성되었습니다.");
        } catch (PostNotFoundException | UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 작성 중 오류가 발생했습니다.");
        }

        return "redirect:/" + getBoardName(postId) + "/" + postId;
    }

    /**
     * 댓글 수정 폼으로 이동
     */
    @GetMapping("/edit/{commentId}")
    public String editCommentForm(@PathVariable Long commentId,
                                  @PathVariable Long postId,
                                  Model model,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        // 사용자 ID 가져오기 (세션에서)
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        Comment comment=commentService.getCommentById(commentId);
        model.addAttribute("postId", postId);
        // 댓글 조회 및 권한 확인은 서비스 레이어에서 처리
        // 단순히 댓글 정보를 조회하여 수정 폼에 전달
        try {
            CommentEditDTO commentEditDTO = CommentEditDTO.fromEntity(comment);
            model.addAttribute("commentEditDTO", commentEditDTO);
            return "fragments/commentEditForm :: commentEditForm"; // 수정 폼 템플릿 경로
        } catch (PostNotFoundException | UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/"; // 적절한 리다이렉트 경로로 수정
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 수정 중 오류가 발생했습니다.");
            return "redirect:/";
        }
    }

    /**
     * 댓글 수정 처리
     */
    @PostMapping("/edit")
    public String editComment(@PathVariable Long postId,
                              @RequestParam Long commentId,
                              @RequestParam String newContent,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {

        // 사용자 ID 가져오기 (세션에서)
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        try {
            // 댓글 수정
            commentService.editComment(userId, newContent, commentId);
            redirectAttributes.addFlashAttribute("successMessage", "댓글이 성공적으로 수정되었습니다.");
        } catch (PostNotFoundException | UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 수정 중 오류가 발생했습니다.");
        }

        // 수정된 댓글이 속한 게시글로 리다이렉트
        Post post = postService.getCurrentPost(postId);
        return "redirect:/" + post.getBoard().getBoardName() + "/" + postId;
    }

    /**
     * 댓글 삭제 처리
     */
    @PostMapping("/delete")
    public String deleteComment(@PathVariable Long postId,
                                @RequestParam Long commentId,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        // 사용자 ID 가져오기 (세션에서)
        Long userId = (Long) session.getAttribute("userId");
        Post post=postService.getCurrentPost(postId);
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/login";
        }

        try {
            // 댓글 삭제
            commentService.deleteComment(commentId, userId);
            redirectAttributes.addFlashAttribute("successMessage", "댓글이 성공적으로 삭제되었습니다.");
            return "redirect:/" + post.getBoard().getBoardName() + "/" + post.getPostId();
        } catch (PostNotFoundException | UnauthorizedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 삭제 중 오류가 발생했습니다.");
            return "redirect:/";
        }
    }

    /**
     * 게시글 ID를 통해 보드 이름을 가져오는 헬퍼 메소드
     */
    private String getBoardName(Long postId) {
        Post post = postService.getCurrentPost(postId);
        return post.getBoard().getBoardName();
    }
}

