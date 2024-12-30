package com.example.myCommunity.Controller;

import com.example.myCommunity.domain.Board;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.service.BoardService;
import com.example.myCommunity.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final PostService postService;



    /**
     * 개별 게시판 페이지 - 특정 게시판의 모든 글 목록을 페이지네이션하여 표시
     *
     * @param boardName     게시판 이름
     * @param page     현재 페이지 번호 (0부터 시작)
     * @param size     페이지당 글 수
     * @param model    모델 객체
     * @return         게시판 템플릿
     */
    @GetMapping("/boards/{boardName}")
    public String boardPage(
            @PathVariable("boardName") String boardName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Board board = boardService.getBoardByName(boardName);
        Page<Post> postPage = postService.getPostsByBoard(board, PageRequest.of(page, size));

        model.addAttribute("board", board);
        model.addAttribute("postPage", postPage);
        return "post/post";
    }
}
