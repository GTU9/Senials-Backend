package com.senials.suggestion.controller;


import com.senials.common.ResponseMessage;
import com.senials.config.HttpHeadersFactory;
import com.senials.suggestion.dto.SuggestionDTO;
import com.senials.suggestion.entity.Suggestion;
import com.senials.suggestion.service.SuggestionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class SuggestionController {

    private final SuggestionService suggestionService;

    private final HttpHeadersFactory httpHeadersFactory;

    public SuggestionController(SuggestionService suggestionService,
                                HttpHeadersFactory httpHeadersFactory) {
        this.suggestionService = suggestionService;
        this.httpHeadersFactory=httpHeadersFactory;
    }

    @Value("${jwt.secret}")
    private String secretKey;
    // JWT에서 userNumber를 추출하는 메서드
    private int extractUserNumberFromToken(String token) {
        // JWT 디코딩 로직 (예: jjwt 라이브러리 사용)
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey) // 비밀 키 설정
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userNumber", Integer.class);
    }

    //건의 생성
    @PostMapping("/suggestion")
    public ResponseEntity<ResponseMessage> createSuggestion(
            @RequestBody SuggestionDTO suggestionDTO,
            @RequestHeader("Authorization") String token){

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        int userNumber = extractUserNumberFromToken(token); // 토큰에서 userNumber 추출
        Suggestion suggestion=suggestionService.saveSuggestion(suggestionDTO, userNumber);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("suggestion", suggestion);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "생성 성공", responseMap));
    }

    //건의 리스트 조회
    @GetMapping("/suggestion")
    public ResponseEntity<ResponseMessage> getSuggestion(){
        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        List<SuggestionDTO> suggestionDTOList =suggestionService.getSuggestionList();

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("suggestionDTOList", suggestionDTOList);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "생성 성공", responseMap));
    }

    //특정 건의 조회
    @GetMapping("/suggestion/{suggestionNumber}")
    public ResponseEntity<ResponseMessage> getSuggestionById(@PathVariable("suggestionNumber") int suggestionNumber){
        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        SuggestionDTO suggestionDTO=suggestionService.getSuggestionById(suggestionNumber);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("suggestionDTO", suggestionDTO);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "조회 성공", responseMap));
    }

    //특정 건의 삭제
    @DeleteMapping("/suggestion/{suggestionNumber}")
    public ResponseEntity<ResponseMessage> deleteSuggestionById(@PathVariable("suggestionNumber") int suggestionNumber) {
        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        suggestionService.deleteSuggestionById(suggestionNumber);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "삭제 성공",null));
    }
}
