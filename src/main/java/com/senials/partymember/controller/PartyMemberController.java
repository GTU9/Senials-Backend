package com.senials.partymember.controller;

import com.senials.common.ResponseMessage;
import com.senials.common.TokenParser;
import com.senials.config.HttpHeadersFactory;
import com.senials.partymember.PartyMemberDTO;
import com.senials.partymember.service.PartyMemberService;
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
public class PartyMemberController {

    private final TokenParser tokenParser;
    private final HttpHeadersFactory httpHeadersFactory;
    private final PartyMemberService partyMemberService;


    public PartyMemberController(
            TokenParser tokenParser
            , PartyMemberService partyMemberService
            , HttpHeadersFactory httpHeadersFactory
    ) {
        this.tokenParser = tokenParser;
        this.partyMemberService = partyMemberService;
        this.httpHeadersFactory = httpHeadersFactory;
    }


    /* 모임 멤버 페이지 조회 */
    @GetMapping("/partyboards/{partyBoardNumber}/partymembers-page")
    public ResponseEntity<ResponseMessage> getPartyMembersPage (
            @PathVariable Integer partyBoardNumber
            , @RequestParam(required = false, defaultValue = "4") Integer pageSize
            , @RequestParam(required = false, defaultValue = "0") Integer pageNumber
            , @RequestHeader(name = "Authorization") String token
    ) {
        int userNumber = tokenParser.extractUserNumberFromToken(token);
        List<PartyMemberDTO> partyMemberDTOList = partyMemberService.getPartyMembers(userNumber, partyBoardNumber, pageNumber, pageSize);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("partyMembers", partyMemberDTOList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "모임 멤버 전체 조회 성공", responseMap));
    }


    /* 모임 멤버 전체 조회 */
    @GetMapping("/partyboards/{partyBoardNumber}/partymembers")
    public ResponseEntity<ResponseMessage> getPartyMembers (
            @PathVariable Integer partyBoardNumber
    ) {
        List<UserDTOForPublic> userDTOForPublicList = partyMemberService.getPartyMembers(partyBoardNumber);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("partyMembers", userDTOForPublicList);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "모임 멤버 전체 조회 성공", responseMap));
    }


    /* 모임 참가 */
    @PostMapping("/partyboards/{partyBoardNumber}/partymembers")
    public ResponseEntity<ResponseMessage> registerPartyMember (
            @PathVariable Integer partyBoardNumber
            , @RequestHeader(value = "Authorization") String token
    ) {

        int code = 2;
        if(token != null) {
            int userNumber = tokenParser.extractUserNumberFromToken(token);
            code = partyMemberService.registerPartyMember(userNumber, partyBoardNumber);
        }

        String message = null;
        if(code == 1) {
            message = "참가 성공";
        } else if(code == 0) {
            message = "요청 실패 (관리자에게 문의)";
        } else {
            message = "로그인 필요";
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, message, responseMap));
    }

    /* 모임 탈퇴 */
    @DeleteMapping("/partyboards/{partyBoardNumber}/partymembers")
    public ResponseEntity<ResponseMessage> unregisterPartyMember (
            @PathVariable Integer partyBoardNumber
            , @RequestHeader(name = "Authorization") String token
    ) {
        int userNumber = tokenParser.extractUserNumberFromToken(token);

        partyMemberService.unregisterPartyMember(userNumber, partyBoardNumber);

        Map<String, Object> responseMap = new HashMap<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "모임 탈퇴 성공", responseMap));
    }

    /* 모임 추방 */
    @PutMapping("/partyboards/{partyBoardNumber}/partymembers")
    public ResponseEntity<ResponseMessage> kickPartyMember (
            @PathVariable Integer partyBoardNumber
            , @RequestBody List<Integer> kickList
            , @RequestHeader(value = "Authorization") String token
    ) {
        int userNumber = tokenParser.extractUserNumberFromToken(token);

        partyMemberService.kickPartyMember(userNumber, kickList, partyBoardNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "추방 성공", null));
    }
}
