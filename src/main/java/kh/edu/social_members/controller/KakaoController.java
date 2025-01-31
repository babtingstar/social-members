package kh.edu.social_members.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class KakaoController {
    // kakao Dev 에서 application name = MyApp
    // Redirect URI = /login/oauth2/code/kakao
    // WEB URI = http://localhost:8085/

    // 필수 동의 항목 : 프로필정보(닉네임), 이름, 이메일
    // 추가 동의 항목 : 프로필사진, 성별

    @Value("${kakao.client-id}") // ${REST_API_KEY}
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;

    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<?> getKakaoLoginUrl() { // ResponseEntity<?> 작성을 안해도 됨 현재 제대로 진행되고 있는지 상태 확인일 뿐
        // 카카오톡 개발 문서 에서 카카오로그인 > 예제 > 요청에 작성된 주소를 그대로 가져온 상태
        String url = "https://kauth.kakao.com/oauth/authorize?response_type=code" +
                "&client_id=" + kakaoClientId +
                "&redirect_uri=" + redirectUri;
        return ResponseEntity.ok(url);
    }

    @GetMapping("/login/oauth2/code/kakao")
    public String handleCallback(@RequestParam String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        if (kakaoClientSecret != null) {
            params.add("client_secret", kakaoClientSecret);
        }

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        String accessToken = (String) response.getBody().get("access_token");

        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userResponse = restTemplate.postForEntity(userInfoUrl, userRequest, Map.class);

        Map userInfo = userResponse.getBody();
        System.out.println("===== [Controller] - user info =====");
        System.out.println(userInfo);
        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
        String profileImage = (String) properties.get("profile_image");
        String nickname = (String) properties.get("nickname");
        String encodedNickname = URLEncoder.encode(nickname, StandardCharsets.UTF_8);
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String name = (String) kakaoAccount.get("name");
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        String gender = (String) kakaoAccount.get("gender");

        return "redirect:/signup?nickname=" + encodedNickname
                + "&email=" + email
                + "&name=" + encodedName
                + "&gender=" + gender
                + "&profile_image=" + profileImage;

        /* signup.html 에서 회원가입란을 작성하지 않고,
        카카오 로그인 클릭 후 바로 DB에 저장하는 방식

        예전에는 아래와 같은 방식을 주로 사용
        로그인하는 회사별로 사용하는 json 형식을 모두 파악
        service 에서 개발자가 처리하는 로직에서 문제가 발생
        DB에 값이 제대로 넘어오지 않는 경우 존재 -> 소셜 변수명 변경, JSON 형식 변경했을 때

        memberService.insertMember(nickname, name, email, gender);
        return "DB에 저장완료";

         */
    }
}
