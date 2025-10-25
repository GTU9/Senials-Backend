package com.senials.meet.controller;

import com.senials.common.ResponseMessage;
import com.senials.common.TokenParser;
import com.senials.config.HttpHeadersFactory;
import com.senials.meet.dto.MeetDTO;
import com.senials.meet.dto.MeetDTOForMember;
import com.senials.meet.repository.MeetRepository;
import com.senials.meet.service.MeetService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MeetController {

    private final TokenParser tokenParser;
    private final HttpHeadersFactory httpHeadersFactory;
    private Integer loggedInUserNumber = 3;
    private final MeetService meetService;
    private final MeetRepository meetRepository;


    @Autowired
    public MeetController(
            TokenParser tokenParser
            , MeetService meetService
            , MeetRepository meetRepository
            , HttpHeadersFactory httpHeadersFactory
    ) {
        this.tokenParser = tokenParser;
        this.meetService = meetService;
        this.meetRepository = meetRepository;
        this.httpHeadersFactory = httpHeadersFactory;
    }

    /* 모임 일정 조회 */
    @GetMapping("/meets/{meetNumber}")
    public ResponseEntity<ResponseMessage> getMeetByMeetNumber(
            @PathVariable Integer meetNumber
    ) {
        MeetDTOForMember meetDTO = meetService.getMeetByNumber(meetNumber);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("meet" , meetDTO);
        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "일정 조회 성공", responseMap));
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

    /* 모임 내 일정 전체 조회 (로그인 중일시 참여여부 까지) */
    @GetMapping("/partyboards/{partyBoardNumber}/meets")
    public ResponseEntity<ResponseMessage> getMeetsByPartyBoardNumber(
            @PathVariable Integer partyBoardNumber
            , @RequestParam(required = false, defaultValue = "4") Integer pageSize
            , @RequestParam(required = false, defaultValue = "0") Integer pageNumber
            ,@RequestHeader(required = false, value = "Authorization") String token

    ) {
        Integer userNumber = null;
        if(token != null) {
            userNumber  = tokenParser.extractUserNumberFromToken(token);
        }

        // 일정 정보 조회
        List<MeetDTO> meetDTOList = meetService.getMeetsByPartyBoardNumber(userNumber, partyBoardNumber, pageNumber, pageSize);

        // 데이터 더 남아 있는지 여부 검사
        Map<String, Object> responseMap = new HashMap<>();
        int totalCnt = meetService.countMeets(partyBoardNumber);
        boolean hasMore = (pageNumber + 1) * pageSize < totalCnt;

        responseMap.put("meets", meetDTOList);
        responseMap.put("hasMore", hasMore);

        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "일정 전체 조회 완료", responseMap));
    }

    /* 특정 일정 조회 */
    @GetMapping("/partyboards/{partyBoardNumber}/meets/{meetNumber}")
    public ResponseEntity<ResponseMessage> getMeetByNumber(
            @PathVariable Integer partyBoardNumber,
            @PathVariable Integer meetNumber
    ) {
        MeetDTO meetDTO = meetService.getMeetByNumber(partyBoardNumber, meetNumber);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("meet", meetDTO);

        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "특정 일정 조회 완료", responseMap));
    }

    /* 모임 일정 추가 */
    @PostMapping("/partyboards/{partyBoardNumber}/meets")
    public ResponseEntity<ResponseMessage> registerMeet(
            @PathVariable Integer partyBoardNumber,
            @RequestBody MeetDTO meetDTO
    ) {
        meetService.registerMeet(partyBoardNumber, meetDTO);

        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "일정 추가 완료", null));
    }


    /* 모임 일정 수정 */
    /* meetNumber 만으로 고유하기 때문에 partyBoardNumber는 필요없음 */
    @PutMapping("/partyboards/{partyBoardNumber}/meets/{meetNumber}")
    public ResponseEntity<ResponseMessage> modifyMeet (
            @PathVariable Integer meetNumber
            , @RequestBody MeetDTO meetDTO
    ) {
        meetService.modifyMeet(meetNumber, meetDTO);

        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "일정 수정 완료", null));
    }


    /* 모임 일정 삭제 */
    @DeleteMapping("/partyboards/{partyBoardNumber}/meets/{meetNumber}")
    public ResponseEntity<ResponseMessage> removeMeet (
            @PathVariable Integer meetNumber
    ) {
        meetService.removeMeet(meetNumber);

        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "일정 삭제 완료", null));
    }
        
    @GetMapping("/users/{userNumber}/meets")
    public ResponseEntity<List<MeetDTO>> getUserMeets(@PathVariable int userNumber) {
        List<MeetDTO> meets = meetService.getMeetsByUserNumber(userNumber);
        return ResponseEntity.ok(meets);
        
    }
}
