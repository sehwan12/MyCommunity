package com.example.myCommunity.Controller;

import com.example.myCommunity.Exception.UserNotFoundException;
import com.example.myCommunity.domain.Users;
import com.example.myCommunity.dto.userDto.UserLoginDTO;
import com.example.myCommunity.dto.userDto.UserRegistrationDTO;
import com.example.myCommunity.dto.userDto.UserResponseDTO;
import com.example.myCommunity.dto.userDto.UserUpdateDTO;
import com.example.myCommunity.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
/**
 * @RequestParam
1:1로 값을 바인딩 해줍니다.
만약 1:1로 받는값이 메서드 매개변수의 생성자(1개의 인자만 받는)의 인자와 일치하면 객체를 바로 바인딩 해줍니다.
잘못된 파라미터값이 들어오면 400 BadRequest를 발생시킵니다.
@ModelAttribute
여러 파라미터를 매개변수에 바인딩해줄 수 있습니다.
setter를 사용해 담아주기 때문에 해당 매개변수의 바인딩 받는 필드는 setter가 있어야 합니다.
타입 변환에 실패하더라도 작업은 계속됩니다. (WebExchangeBindException 발생
@RequestBody
Post 요청시 기본생성자가 있어야하며 Setter는 없어도 된다
Get 요청시 Setter가 존재해야 한다.(기본 WebDataBinder 설정, 변경가능)
*
* */

//BindingResult가 있으면 @ModelAttribute에 데이터 바인딩 오류가 발생했을 때
//BindingResult에 오류 정보가 담기게 되고 Controller가 정상 호출
//FieldError는 BindingResult에 보관하는 오류 객체.
//필드의 타입이 맞지 않을 때 스프링이 생성할 수도있고, 개발자가 검증을 수행해서
//필드에 오류가 있다면 직접 생성해서 BindingResult의 addError()메서드를 통해 넣을수 있음
//FieldError 를 직접 생성하지 않고 필드 오류를 처리할 수 있도록 단순화 해주는 rejectValue() 메서드를 제공

/** http프로토콜은 이전 페이지에서 수집한 데이터를 다음페이지에서 사용x
* 데이터를 유지하기 위해선 어딘가 저장해놓고 유지해야함
* 저장소: HttpServletRequest, HttpSession, ServletContext
* 저장: setAttribute(name, value)/추출: getAttribute(name)/ 삭제: removeAttribute(name)
* HttpSession: 생성 Client 최초 접속시/ 제거 Client 접속 종료 시 / 로그인-로그아웃 장바구니 등
* HttpServletRequest: 생성 Client가 요청시/ 삭제 Server가 응답 시/Request중인 동안에는 존재
*
* */

//아직 미완성
@RequiredArgsConstructor
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 회원가입 페이지 표시
    @GetMapping("/register")
    public String registerForm(Model model){
        //addAttribute(String name, Object value): value 객체를 name 이름으로 추가해줌
        //View에서 name으로 지정된 value를 사용
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "users/register";
    }

    // 회원가입 처리
    //RedirectAttributes를 사용하여 리다이렉트 시 플래시 속성으로 메시지를 전달
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserRegistrationDTO registrationDTO,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "users/register"; // 유효성 검사 실패 시 회원가입 페이지로 돌아감
        }

        try {
            Long savedId = userService.registerUser(registrationDTO);
            Users user=userService.getUserById(savedId);
            String username=user.getUsername();
            redirectAttributes.addFlashAttribute("successMessage",
                    username+"님, 회원가입이 성공적으로 완료되었습니다. 로그인해주세요.");
            return "redirect:/users/login"; // 회원가입 성공 시 로그인 페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "users/register";
        }
    }

    //마이페이지 표시
    //httpsession을 메소드에서 매개변수를 통해 주입받는 형식인 경우, 선언시에 Servlet Container에게 Session을 달라고 요청한다.
    //클라이언트가 처음 서버에 연결을 하면 어떤 하나의 Session ID가 생성된다
    @GetMapping("/mypage")
    public String showMyPage(Model model, HttpSession session) {
        // 세션 또는 인증된 사용자 정보로부터 userId를 가져오는 대신 경로 변수로 받습니다.

        // 세션의 사용자와 요청된 userId가 일치하는지 확인 (보안상 필요)
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null ) {
            return "redirect:/users/login"; // 또는 에러 페이지로 리다이렉트
        }

        Users user = userService.getUserById(sessionUserId);
        UserResponseDTO responseDTO=UserResponseDTO.fromEntity(user);
        model.addAttribute("user", responseDTO);
        return "users/mypage"; // mypage.html 뷰를 반환
    }

    //마이페이지 수정
    @PostMapping("/{userId}/mypage")
    public String updateMyPage(@PathVariable Long userId,
                               @Valid @ModelAttribute UserUpdateDTO updateDTO,
                               BindingResult bindingResult,
                               Model model,
                               HttpSession session,
                               RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "users/mypage";
        }

        // 세션의 사용자와 요청된 userId가 일치하는지 확인
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            return "redirect:/users/login"; // 또는 에러 페이지로 리다이렉트
        }

        try {
            userService.updateUser(updateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "정보가 성공적으로 수정되었습니다.");
            return "redirect:/users/" + userId + "/mypage"; // 수정 후 마이페이지로 리다이렉트
        } catch (UserNotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "users/mypage"; // 오류 발생 시 다시 마이페이지로
        }
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
            Long userId = userService.login(loginDTO);
            // 세션에 사용자 정보 저장
            session.setAttribute("userId", userId);
            return "redirect:/users/mypage"; // 로그인 성공 시 마이페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "users/login";
        }
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // 세션 무효화
        session.invalidate();

        // 로그아웃 성공 메시지 추가
        redirectAttributes.addFlashAttribute("successMessage", "성공적으로 로그아웃 되었습니다.");

        // 로그인 페이지로 리다이렉트
        return "redirect:/users/login";
    }
}
