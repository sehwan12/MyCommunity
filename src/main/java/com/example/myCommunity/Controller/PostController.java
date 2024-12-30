package com.example.myCommunity.Controller;

import com.example.myCommunity.Exception.PostNotFoundException;
import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.Board;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.domain.Users;
import com.example.myCommunity.dto.postDTO.PostEditDTO;
import com.example.myCommunity.dto.postDTO.PostRegistrationDTO;
import com.example.myCommunity.dto.postDTO.PostResponseDTO;
import com.example.myCommunity.dto.userDto.UserResponseDTO;
import com.example.myCommunity.service.BoardService;
import com.example.myCommunity.service.PostService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//일반적으로 세션에는 사용자정보만 저장하며 Postid는 세션에 저장하지 않습니다.
//세션에서 Userid를 가져와 현재 사용자의 권한을 확인

@RequiredArgsConstructor
@Controller
@RequestMapping("/{boardName}")
public class PostController {

    private final PostService postService;
    private final BoardService boardService;

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
                          RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "post/newpost";
        }

        try{
            postRegistrationDTO.setBoardName(boardName);
            Long newPostId = postService.addPost(postRegistrationDTO);
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

        PostResponseDTO responseDTO=PostResponseDTO.fromEntity(post);
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
                           HttpSession session){
        if(bindingResult.hasErrors()){
            //돌아갈 페이지 표시(임시)
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
            redirectAttributes.addFlashAttribute("successMessage", "정보가 성공적으로 수정되었습니다.");
            return "redirect:/" + boardName + "/" + postId; // 수정 후 해당 글 페이지로 리다이렉트
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
             // 오류 발생 시 다시 해당글로
            return "post/editpost";
        }
    }

    // PostController.java
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
