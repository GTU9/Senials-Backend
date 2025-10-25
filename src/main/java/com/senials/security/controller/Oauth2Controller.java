package com.senials.security.controller;

import com.senials.common.ResponseMessage;
import com.senials.config.HttpHeadersFactory;
import com.senials.security.domain.kakao.auth.PrincipalDetails;
import com.senials.security.repository.SecurityUserRepository;
import com.senials.security.service.PrincipalOauth2UserService;
import com.senials.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.senials.security.service.OAuth2Service;
import com.senials.security.service.JwtService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class Oauth2Controller {
    private final SecurityUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession httpSession;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private HttpHeadersFactory httpHeadersFactory;
    @Autowired
    private OAuth2Service oAuth2Service;
    @Autowired
    private JwtService jwtService;


    public Oauth2Controller(SecurityUserRepository userRepository, PasswordEncoder passwordEncoder, PrincipalOauth2UserService principalOauth2UserService, HttpSession httpSession) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.httpSession = httpSession;
    }

    @GetMapping("/login")
    @ResponseBody
    public String loginForm() {
        return "login";
    }

    @GetMapping("/join")
    public String joinForm() {
        return "join";
    }

    @GetMapping("/fail")
    @ResponseBody
    public ResponseEntity<String> loginFail()
    {
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("로그인 실패");
    }


    @GetMapping("/oauth/loginInfo")
    @ResponseBody
    public String oauthLoginInfo(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2UserPrincipal) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "사용자가 인증되지 않았습니다.";
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        return attributes.toString();
    }

    @PostMapping("/join")
    @ResponseBody
    public ResponseEntity<?> join(@RequestBody User user) {
        // 가입 요청 로깅
        System.out.println("가입 요청: " + user);

        // 사용자 이름 중복 확인
        if (userRepository.existsByUserName(user.getUserName())) {
            return ResponseEntity.badRequest().body(Map.of("error", "이미 사용 중인 사용자 이름입니다."));
        }

        // 비밀번호가 null인지 확인
        if (user.getUserPwd() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "비밀번호는 null일 수 없습니다."));
        }

        // 비밀번호를 변수에 저장
        String rawPassword = user.getUserPwd();

        // 비밀번호 암호화
        String encodePassword = passwordEncoder.encode(rawPassword);
        user.initializePwd(encodePassword); // 사용자 객체에 암호화된 비밀번호 저장
        user.initializeSignupDate(); // 가입 날짜 초기화
        user.initializeUuid(); // UUID 초기화

        System.out.println("생성된 UUID: " + user.getUserUuid());

        // 사용자 정보를 저장
        userRepository.save(user);

        // 자동 로그인 처리
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), rawPassword)); // 입력된 비밀번호 사용

            // SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            System.out.println("자동 로그인 실패: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of("message", "회원가입 성공")); // 성공 응답 반환
    }

    @GetMapping("/loginInfo")
    @ResponseBody
    public String loginInfo(Authentication authentication, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "사용자가 인증되지 않았습니다.";
        }

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return "OAuth2 로그인 : " + principal;
    }

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId; // 카카오 클라이언트 ID

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri; // 카카오 리디렉션 URI

    @GetMapping("/api/init-kakao-login")
    public ResponseEntity<Map<String, String>> initKakaoLogin() {
        try {
            String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString());
            String authUrl = "https://kauth.kakao.com/oauth/authorize?client_id=" + clientId + "&redirect_uri=" + encodedRedirectUri + "&response_type=code";
            Map<String, String> response = new HashMap<>();
            response.put("authUrl", authUrl);
            System.out.println("전달준비완료");
            System.out.println("Auth URL: " + authUrl); // URL 확인
            return ResponseEntity.ok(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Encoding error"));
        }
    }
}
