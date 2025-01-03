package com.example.myCommunity.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void addSessionAttributes(Model model, HttpSession session) {
        Long sessionUserId = (Long) session.getAttribute("userId");
        model.addAttribute("sessionUserId", sessionUserId);
    }
}
