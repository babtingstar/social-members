package kh.edu.social_members.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// url - html 템플릿 연동 설정
@Controller
public class ViewController {
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/signup")
    public String signup(@RequestParam("name") String name,
                         @RequestParam("email") String email,
                         @RequestParam("nickname") String nickname,
                         @RequestParam("gender") String gender,
                         Model model) {
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("nickname", nickname);
        model.addAttribute("gender", gender);
        return "signup";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
