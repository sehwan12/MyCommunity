package com.example.myCommunity.Controller;

import com.example.myCommunity.dto.UserLoginDTO;
import com.example.myCommunity.dto.UserRegistrationDTO;
import com.example.myCommunity.dto.UserResponseDTO;
import com.example.myCommunity.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


//아직 미완성
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired // 생성자 주입
    public UserController(UserService userService) {
        this.userService = userService;
    }
    // 회원가입 페이지 표시
    @GetMapping("/register")
    public String registerForm(Model model){
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "users/register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRegistrationDTO registrationDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "users/register"; // 유효성 검사 실패 시 회원가입 페이지로 돌아감
        }

        try {
            UserResponseDTO savedUser = userService.registerUser(registrationDTO);
            model.addAttribute("successMessage", savedUser.getUserId()+"님, 회원가입이 성공적으로 완료되었습니다. 로그인해주세요.");
            return "redirect:/users/login"; // 회원가입 성공 시 로그인 페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "users/register";
        }
    }

    //마이페이지 표시
    @GetMapping("/{userId}/mypage")
    public String showMyPage(@PathVariable Integer userId, Model model, HttpSession session) {
        // 세션 또는 인증된 사용자 정보로부터 userId를 가져오는 대신 경로 변수로 받습니다.

        // 세션의 사용자와 요청된 userId가 일치하는지 확인 (보안상 필요)
        Integer sessionUserId = (Integer) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            return "redirect:/users/login"; // 또는 에러 페이지로 리다이렉트
        }

        UserResponseDTO user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "users/mypage"; // mypage.html 뷰를 반환
    }

    //마이페이지 수정
    @PostMapping("/{userId}/mypage")
    public String updateMyPage(@PathVariable Integer userId,
                               @Valid @ModelAttribute UserRegistrationDTO registrationDTO,
                               BindingResult bindingResult,
                               Model model,
                               HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "users/mypage";
        }

        // 세션의 사용자와 요청된 userId가 일치하는지 확인
        Integer sessionUserId = (Integer) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            return "redirect:/users/login"; // 또는 에러 페이지로 리다이렉트
        }

        UserResponseDTO updatedUser = userService.updateUser(userId, registrationDTO);
        model.addAttribute("user", updatedUser);
        model.addAttribute("successMessage", "정보가 성공적으로 수정되었습니다.");
        return "users/mypage";
    }

    //로그인 페이지 표시
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userLoginDTO", new UserLoginDTO());
        return "users/login";
    }

    //로그인 로직
    //이후 수정
    @PostMapping("/login")
    public String login(@Valid @ModelAttribute UserLoginDTO loginDTO,
                        BindingResult bindingResult,
                        Model model,
                        HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "users/login"; // 유효성 검사 실패 시 다시 로그인 페이지로
        }

        try {
            UserResponseDTO user = userService.login(loginDTO);
            // 세션에 사용자 정보 저장
            session.setAttribute("userId", user.getUserId());
            return "redirect:/users/" + user.getUserId() + "/mypage"; // 로그인 성공 시 마이페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "users/login";
        }
    }



}
