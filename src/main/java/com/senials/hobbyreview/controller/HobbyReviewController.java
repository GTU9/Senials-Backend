package com.senials.hobbyreview.controller;

import com.senials.common.ResponseMessage;
import com.senials.config.HttpHeadersFactory;
import com.senials.hobbyreview.dto.HobbyReviewDTO;
import com.senials.hobbyreview.entity.HobbyReview;
import com.senials.hobbyreview.service.HobbyReviewService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HobbyReviewController {

//    int userNumber=1;

    private final HobbyReviewService hobbyReviewService;

    private final HttpHeadersFactory httpHeadersFactory;

    public HobbyReviewController(HobbyReviewService hobbyReviewService, HttpHeadersFactory httpHeadersFactory){
        this.hobbyReviewService=hobbyReviewService;
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

    /* 취미 후기 단일 조회 */
    @GetMapping("/hobbyreviews/{hobbyReviewNumber}")
    public ResponseEntity<ResponseMessage> getHobbyReviewForReport(
            @PathVariable int hobbyReviewNumber
    ) {
        HobbyReviewDTO hobbyReviewDTO= hobbyReviewService.getHobbyReviewForReport(hobbyReviewNumber);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("hobbyReview", hobbyReviewDTO);

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "취미 후기 조회 성공", responseMap));

    }

    //취미 후기 조회
    @GetMapping("{hobbyNumber}/hobby-review/{hobbyReviewNumber}")
    public ResponseEntity<ResponseMessage> getHobbyReview(
            @PathVariable("hobbyNumber") int hobbyNumber,
            @PathVariable("hobbyReviewNumber") int hobbyReviewNumber,
            @RequestHeader("Authorization") String token) {

        int userNumber = extractUserNumberFromToken(token);
        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

       HobbyReviewDTO hobbyReviewDTO= hobbyReviewService.getHobbyReview(hobbyNumber,userNumber,hobbyReviewNumber);

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("hobbyReview",hobbyReviewDTO);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "조회 성공", responseMap));
    }

    //취미 후기 작성
    @PostMapping("/{hobbyNumber}/hobby-review")
    public ResponseEntity<ResponseMessage> createHobbyReview(
            @RequestBody HobbyReviewDTO hobbyReviewDTO,
            @PathVariable("hobbyNumber") int hobbyNumber,
            @RequestHeader("Authorization") String token) { // JWT 토큰을 헤더로 받기

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        int userNumber = extractUserNumberFromToken(token); // 토큰에서 userNumber 추출
        hobbyReviewDTO.setUserNumber(userNumber); // DTO에 userNumber 설정

        HobbyReview hobbyReview = hobbyReviewService.saveHobbyReview(hobbyReviewDTO, userNumber, hobbyNumber);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("hobbyReview", hobbyReview);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(201, "생성 성공", responseMap));
    }



    //취미 후기 삭제
    @DeleteMapping("/{hobbyNumber}/hobby-review/{hobbyReviewNumber}")
    public ResponseEntity<ResponseMessage> deleteHobbyReview(
            @PathVariable("hobbyNumber") int hobbyNumber,
            @PathVariable("hobbyReviewNumber") int hobbyReviewNumber,
            @RequestHeader("Authorization") String token) {

        int userNumber = extractUserNumberFromToken(token);
        try {
            hobbyReviewService.deleteHobbyReview(hobbyReviewNumber, userNumber, hobbyNumber);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "삭제 성공");

            return ResponseEntity.ok()
                    .headers(httpHeadersFactory.createJsonHeaders())
                    .body(new ResponseMessage(204, "삭제 성공", responseMap));
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("message", e.getMessage());

            return ResponseEntity.badRequest()
                    .headers(httpHeadersFactory.createJsonHeaders())
                    .body(new ResponseMessage(400, "삭제 실패", errorMap));
        }
    }

    //취미 후기 수정
    @PutMapping("{hobbyNumber}/hobby-review/{hobbyReviewNumber}")
    public ResponseEntity<ResponseMessage> updateHobbyReview(
            @PathVariable("hobbyNumber") int hobbyNumber,
            @PathVariable("hobbyReviewNumber") int hobbyReviewNumber,
            @RequestBody HobbyReviewDTO hobbyReviewDTO,
            @RequestHeader("Authorization") String token) {
        int userNumber = extractUserNumberFromToken(token);
        try {
            hobbyReviewService.updateHobbyReview(hobbyReviewDTO,hobbyReviewNumber, userNumber, hobbyNumber);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("message", "수정 성공");

            return ResponseEntity.ok()
                    .headers(httpHeadersFactory.createJsonHeaders())
                    .body(new ResponseMessage(200, "수정 성공", responseMap));
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("message", e.getMessage());

            return ResponseEntity.badRequest()
                    .headers(httpHeadersFactory.createJsonHeaders())
                    .body(new ResponseMessage(400, "수정 실패", errorMap));
        }
    }

}