package kh.edu.social_members.controller;

import kh.edu.social_members.service.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private MemberServiceImpl kakaoService;

    // 카카오톡으로 전달받은 값 -> DB 에 저장
    // PostMapping

}
