package com.example.myCommunity.Controller;

import com.example.myCommunity.domain.Board;
import com.example.myCommunity.domain.Post;
import com.example.myCommunity.service.BoardService;
import com.example.myCommunity.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BoardService boardService;
    private final PostService postService;

    /**
     * 홈페이지 - 모든 게시판과 각 게시판의 최근 글 5개를 표시
     */
    @RequestMapping("/home")
    public String home(Model model, HttpSession session) {
        List<Board> boards=boardService.getAllBoards();
        Map<Long,List<Post>> recentPostsMap=new HashMap<>();
        boards.forEach(board -> {
            List<Post> recentPosts = postService.getRecentPostsByBoard(board, 5);
            recentPostsMap.put(board.getBoardId(), recentPosts);
        });

        // 세션에서 사용자 ID 가져오기 (로그인 기능 구현 시 설정)
        Long sessionUserId = (Long) session.getAttribute("sessionUserId");

        model.addAttribute("boards",boards);
        model.addAttribute("recentPostsMap", recentPostsMap);
        model.addAttribute("sessionUserId", sessionUserId);
        return "home";
    }
}
