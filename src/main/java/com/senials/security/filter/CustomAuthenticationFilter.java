package com.senials.security.filter;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.senials.security.repository.SecurityUserRepository;
import com.senials.security.service.JwtService;
import com.senials.security.service.OAuth2Service;
import com.senials.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.GrantedAuthority;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OAuth2Service oAuth2Service; // OAuth2Service 추가
    private final SecurityUserRepository userRepository; // UserRepository 추가

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService, OAuth2Service oAuth2Service, SecurityUserRepository securityUserRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.oAuth2Service = oAuth2Service; // OAuth2Service 초기화
        this.userRepository = securityUserRepository; // UserRepository 초기화
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("attemptAuthentication 메소드 작동확인");
        String username = null;
        String password = null;

        System.out.println("Received Content-Type: " + request.getContentType()); // 수신 확인

        // Content-Type 확인
        if (request.getContentType() != null && request.getContentType().startsWith("application/json")) {
            // JSON 요청 처리
            StringBuilder sb = new StringBuilder();
            String line;
            try (BufferedReader reader = request.getReader()) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // JSON 파싱
            String json = sb.toString();
            System.out.println("Received JSON: " + json); // JSON 요청 로그
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            // 사용자 이름과 비밀번호 추출
            username = jsonObject.get("userName").getAsString();
            password = jsonObject.get("userPwd").getAsString();

            // 추출된 사용자 이름과 비밀번호 로그
            System.out.println("Parsed username: " + username);
            System.out.println("Parsed password: " + password);
        } else {
            // 폼 데이터 처리
            System.out.println("요기가 돌아가네요");
            username = request.getParameter("userName");
            password = request.getParameter("userPwd");
        }

        System.out.println("attemptAuthentication - 입력된 사용자 이름: " + username);
        System.out.println("attemptAuthentication - 입력된 비밀번호: " + password);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication 메소드 작동확인");
        // SecurityContext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authResult);

        System.out.println("successfulAuthentication - 인증 성공!");
        System.out.println("인증된 유저 : " + authResult.getName());
        System.out.println("인증 권한: " + authResult.getAuthorities());

        try {
            UserDetails userDetails = (UserDetails) authResult.getPrincipal();

            // 필요한 정보를 가져온다
            String username = userDetails.getUsername(); // 사용자 이름
            User user = userRepository.findByUserName(username);
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"사용자를 찾을 수 없습니다.\"}");
                return;
            }
            System.out.println("사용자 이메일 확임다~ : " + user.getUserEmail());
            System.out.println("사용자 성별 확임다~ : " + user.getUserGender());

            // 권한 목록 가져오기
            List<String> roles = authResult.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // JWT 생성 시 추가 정보 포함
            String token = jwtService.generateToken(user, roles);

            response.addHeader("Authorization", "Bearer " + token);
            System.out.println("생성된 JWT: " + token); // JWT 로그 출력

            // JSON 응답 작성
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"인증이 성공했습니다.\", \"token\":\"" + token + "\"}");
            response.setStatus(HttpServletResponse.SC_OK); // HTTP 200 상태 코드
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 출력
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "JWT 생성 실패");
            return; // 예외 발생 시 더 이상 진행하지 않음
        }

        System.out.println("인증 성공 후 SecurityContext에 설정된 사용자 이름: " + authResult.getName());
    }



    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("unsuccessfulAuthentication 메소드 작동확인");
        System.out.println("인증 실패: " + failed.getMessage());
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
