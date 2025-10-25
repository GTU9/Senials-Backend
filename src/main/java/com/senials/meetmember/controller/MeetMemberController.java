package com.senials.meetmember.controller;

import com.senials.common.ResponseMessage;
import com.senials.common.TokenParser;
import com.senials.common.mapper.UserMapper;
import com.senials.config.HttpHeadersFactory;
import com.senials.meet.repository.MeetRepository;
import com.senials.meetmember.repository.MeetMemberRepository;
import com.senials.meetmember.service.MeetMemberService;
import com.senials.user.dto.UserDTOForPublic;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MeetMemberController {

    private final TokenParser tokenParser;
    private final HttpHeadersFactory httpHeadersFactory;
    private final UserMapper userMapper;
    private final MeetRepository meetRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final MeetMemberService meetMemberService;

    public MeetMemberController(
            TokenParser tokenParser
            , HttpHeadersFactory httpHeadersFactory
            , UserMapper userMapper
            , MeetRepository meetRepository
            , MeetMemberRepository meetMemberRepository
            , MeetMemberService meetMemberService
    ) {
        this.tokenParser = tokenParser;
        this.httpHeadersFactory = httpHeadersFactory;
        this.userMapper = userMapper;
        this.meetRepository = meetRepository;
        this.meetMemberRepository = meetMemberRepository;
        this.meetMemberService = meetMemberService;
    }

    /* 모임 일정 참여멤버 조회 */
    @GetMapping("/meets/{meetNumber}/meetmembers")
    public ResponseEntity<ResponseMessage> getMeetMembersByMeetNumber(
            @PathVariable Integer meetNumber
            ,@RequestParam(required = false, defaultValue = "100") Integer pageSize
            ,@RequestParam(required = false, defaultValue = "0") Integer pageNumber
            ,@RequestHeader(name = "Authorization") String token
    ) {

        int userNumber = tokenParser.extractUserNumberFromToken(token);

        List<UserDTOForPublic> meetMemberList = meetMemberService.getMeetMembersByMeetNumber(userNumber, meetNumber, pageNumber, pageSize);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("meetMembers", meetMemberList);

        // ResponseHeader 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "일정 참여멤버 조회 성공", responseMap));
    }

    /* 모임 일정 참여 */
    @PostMapping("/meets/{meetNumber}/meetmembers")
    public ResponseEntity<ResponseMessage> joinMeetMembers(
            @PathVariable Integer meetNumber
            , @RequestHeader(name = "Authorization") String token
    ) {
        int userNumber = tokenParser.extractUserNumberFromToken(token);

        meetMemberService.joinMeetMembers(userNumber, meetNumber);

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "일정 참여 성공", null));
    }


    /* 모임 일정 탈퇴 */
    @DeleteMapping("/meets/{meetNumber}/meetmembers")
    public ResponseEntity<ResponseMessage> quitMeetMembers(
            @PathVariable Integer meetNumber
            , @RequestHeader(name = "Authorization") String token
    ) {
        int userNumber = tokenParser.extractUserNumberFromToken(token);

        meetMemberService.quitMeetMembers(userNumber, meetNumber);

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "일정 탈퇴 성공", null));
    }

}
