package com.example.myCommunity.Controller;

import com.example.myCommunity.Exception.PostNotFoundException;
import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.*;
import com.example.myCommunity.dto.commentDto.CommentEditDTO;
import com.example.myCommunity.dto.postDTO.PostEditDTO;
import com.example.myCommunity.dto.postDTO.PostRegistrationDTO;
import com.example.myCommunity.dto.postDTO.PostResponseDTO;
import com.example.myCommunity.dto.userDto.UserResponseDTO;
import com.example.myCommunity.service.AttachmentService;
import com.example.myCommunity.service.BoardService;
import com.example.myCommunity.service.CommentService;
import com.example.myCommunity.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//일반적으로 세션에는 사용자정보만 저장하며 Postid는 세션에 저장하지 않습니다.
//세션에서 Userid를 가져와 현재 사용자의 권한을 확인

@RequiredArgsConstructor
@Controller
@RequestMapping("/{boardName}")
public class PostController {

    private final PostService postService;
    private final BoardService boardService;
    private final AttachmentService attachmentService;
    private final CommentService commentService;

    //글쓰기 화면 표시
    @GetMapping("/newpost")
    public String addPostForm(@PathVariable String boardName, Model model){
        Board board= boardService.getBoardByName(boardName);
        if(board == null){
            // Board가 존재하지 않으면 에러 페이지로 리다이렉트 또는 예외 처리
            model.addAttribute("errorMessage", "존재하지 않는 게시판입니다.");
            return "error";
        }

        PostRegistrationDTO postRegistrationDTO = PostRegistrationDTO.builder()
                .boardName(boardName) // boardName 설정
                .build();
        model.addAttribute("postRegistrationDTO", postRegistrationDTO);
        return "post/newpost";
    }
    //글쓰기
    @PostMapping("/newpost")
    public String addPost(@PathVariable String boardName,
                          @Valid @ModelAttribute("postRegistrationDTO") PostRegistrationDTO postRegistrationDTO,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes,
                          HttpSession session,
                          @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments ){

        Long sessionUserId = (Long) session.getAttribute("userId");
        if(sessionUserId == null){
            return "redirect:/users/login"; // 로그인하지 않은 사용자는 로그인 페이지로 리다이렉트
        }

        postRegistrationDTO.setBoardName(boardName);
        postRegistrationDTO.setUserId(sessionUserId);

        if(bindingResult.hasErrors()){
            return "post/newpost";
        }

        try{
            Long newPostId = postService.addPost(postRegistrationDTO);
            // 첨부파일이 존재하면 AttachmentService를 통해 저장
            if(!attachments.isEmpty()){
                attachmentService.addAttachmentsToPost(newPostId, sessionUserId, attachments);
            }

            redirectAttributes.addFlashAttribute("successMessage", "글이 등록되었습니다.");
            return "redirect:/" + boardName + "/" + newPostId;
        }catch (Exception e){
            model.addAttribute("errorMessage", e.getMessage());
            return "post/newpost";
        }


    }

    //해당 글 표시
    @GetMapping("/{postId}")
    public String showPostForm(@PathVariable String boardName,
                               @PathVariable Long postId,
                               Model model){

        Post post=postService.getCurrentPost(postId);
        List<Attachment> attachments= attachmentService.getAttachmentsByPostId(postId);
        List<Comment> comments= commentService.getCommentsByPostId(postId);
        PostResponseDTO responseDTO=PostResponseDTO.fromEntity(post,attachments,comments);
        model.addAttribute("post", responseDTO);
        return "post/post"; // mypage.html 뷰를 반환
    }

    //글수정 화면 표시
    @GetMapping("/editPost/{postId}")
    public String editPostForm(@PathVariable String boardName,
                               @PathVariable Long postId,
                               Model model,
                               HttpSession session){

        Long sessionUserId = (Long) session.getAttribute("userId");
        if(sessionUserId == null){
            return "redirect:/users/login";
        }

        Post post = postService.getCurrentPost(postId);
        if(!sessionUserId.equals(post.getUser().getUserId())){
            // 권한이 없는 사용자는 접근 불가
            return "redirect:/" + boardName + "/" + postId;
        }
        List<Attachment> attachments= attachmentService.getAttachmentsByPostId(postId);
        model.addAttribute("attachments", attachments);
        PostEditDTO postEditDTO = PostEditDTO.fromEntity(post);
        model.addAttribute("postEditDTO", postEditDTO);
        return "post/editpost";
    }

    //글수정
    @PostMapping("/editPost/{postId}")
    public String editPost(@PathVariable String boardName,
                           @PathVariable Long postId,
                           @Valid @ModelAttribute PostEditDTO postEditDTO,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           HttpSession session,
                           @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments){
        if(bindingResult.hasErrors()){
            // 오류가 있는 경우 현재 첨부파일을 다시 추가
            List<Attachment> existingAttachments = attachmentService.getAttachmentsByPostId(postId);
            model.addAttribute("attachments", existingAttachments);
            return "post/editpost";
        }

        Long sessionUserId = (Long) session.getAttribute("userId");
        if(sessionUserId == null){
            return "redirect:/users/login";
        }

        Post post = postService.getCurrentPost(postId);
        if(!post.isAuthor(sessionUserId)){
            // 권한이 없는 사용자는 로그인 페이지로 리다이렉트
            return "redirect:/users/login";
        }

        try {
            postService.updatePost(postEditDTO, sessionUserId);
            // 새로운 첨부파일 추가
            attachmentService.addAttachmentsToPost(postId, sessionUserId, attachments);
            redirectAttributes.addFlashAttribute("successMessage", "정보가 성공적으로 수정되었습니다.");
            return "redirect:/" + boardName + "/" + postId; // 수정 후 해당 글 페이지로 리다이렉트
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            // 오류 발생 시 다시 해당글로
            List<Attachment> existingAttachments = attachmentService.getAttachmentsByPostId(postId);
            model.addAttribute("attachments", existingAttachments);
            return "post/editpost";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "게시글 수정 중 오류가 발생했습니다.");
            List<Attachment> existingAttachments = attachmentService.getAttachmentsByPostId(postId);
            model.addAttribute("attachments", existingAttachments);
            return "post/editpost";
        }
    }

    @PostMapping("/deletePost/{postId}")
    public String deletePost(@PathVariable String boardName,
                             @PathVariable Long postId,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             HttpSession session){
        Long sessionUserId = (Long) session.getAttribute("userId");
        if(sessionUserId == null){
            return "redirect:/users/login";
        }

        Post post = postService.getCurrentPost(postId);

        // 권한이 없는 사용자는 게시물 페이지로 리다이렉트
        if(!post.isAuthor(sessionUserId)){
            return "redirect:/" + boardName + "/" + postId;
        }

        try {
            postService.deletePost(postId, sessionUserId);
            redirectAttributes.addFlashAttribute("successMessage", "게시물이 삭제되었습니다.");
            return "redirect:/" + boardName;
        } catch (PostNotFoundException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "error";
        }
    }

}
