package com.senials.hobbyboard.controller;

import com.senials.common.ResponseMessage;
import com.senials.config.HttpHeadersFactory;
import com.senials.favorites.entity.Favorites;
import com.senials.hobbyboard.dto.HobbyDTO;
import com.senials.hobbyboard.dto.HobbyDTOForCard;
import com.senials.hobbyboard.service.HobbyService;
import com.senials.hobbyreview.dto.HobbyReviewDTO;
import com.senials.hobbyreview.service.HobbyReviewService;
import com.senials.partyboard.dto.PartyBoardDTOForDetail;
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
public class HobbyController {

//    int userNumber=1;

    private HobbyService hobbyService;
    private final HttpHeadersFactory httpHeadersFactory;

    private HobbyReviewService hobbyReviewService;

    public HobbyController(HobbyService hobbyService,HobbyReviewService hobbyReviewService, HttpHeadersFactory httpHeadersFactory){

        this.hobbyService=hobbyService;
        this.hobbyReviewService=hobbyReviewService;
        this.httpHeadersFactory = httpHeadersFactory;
    }

    //취미 전체 조회
    @GetMapping("/hobby-board")
    public ResponseEntity<ResponseMessage> findHobbyAll(){

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        List<HobbyDTO> hobbyDTOList = hobbyService.findAll();

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("hobby", hobbyDTOList);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "조회 성공", responseMap));
    }

    //평균 평점 기준, 리뷰 특정 개수 이상 Top3 출력
    @GetMapping("/hobby-board/top3")
    public ResponseEntity<ResponseMessage> hobbySortByRating(
            @RequestParam(required = false, defaultValue = "3") Integer minReviewCnt
            , @RequestParam(required = false, defaultValue = "3") Integer size
    ){

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        List<HobbyDTO> hobbyDTOList = hobbyService.hobbySortByRating(minReviewCnt,size);

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("hobby", hobbyDTOList);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "조회 성공", responseMap));
    }

    //취미 상세 조회, 해당 취미후기 리스트 조회
    @GetMapping("/hobby-detail/{hobbyNumber}")
    public ResponseEntity<ResponseMessage> findHobbyDetail(@PathVariable("hobbyNumber")int hobbyNumber){

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        HobbyDTO hobbyDTO = hobbyService.findById(hobbyNumber);
        List<HobbyReviewDTO> hobbyReviewDTOList=hobbyReviewService.getReviewsListByHobbyNumber(hobbyNumber);

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("hobbyReview",hobbyReviewDTOList);
        responseMap.put("hobby", hobbyDTO);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "조회 성공", responseMap));
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

    //취미 카테고리별 취미 목록 조회
    @GetMapping("/hobby-board/{categoryNumber}")
    public ResponseEntity<ResponseMessage> findHobbyByCategory(@PathVariable("categoryNumber")int categoryNumber){

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        List<HobbyDTO> hobbyDTOList = hobbyService.findByCategory(categoryNumber);

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("hobby", hobbyDTOList);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "조회 성공", responseMap));
    }

    //맞춤형 취미 추천 결과 조회
    @GetMapping("/suggest-hobby-result")
    public ResponseEntity<ResponseMessage> getSuggestHobby(@RequestParam int hobbyAbility,
                                                           @RequestParam int hobbyBudget,
                                                           @RequestParam int hobbyLevel,
                                                           @RequestParam int hobbyTendency) {

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        HobbyDTO hobbyDTO = hobbyService.suggestHobby(hobbyAbility, hobbyBudget,hobbyTendency, hobbyLevel);

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("hobby", hobbyDTO);

        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(201, "생성 성공", responseMap));
    }

    //맞춤형 취미 추천 나의 취미 관심사 등록
    @PostMapping("/suggest-hobby-result")
    public ResponseEntity<ResponseMessage> setSuggestHobby(@RequestParam int hobbyNumber, @RequestHeader("Authorization") String token){
        int userNumber = extractUserNumberFromToken(token);
        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        Favorites favorites=hobbyService.setFavoritesByHobby(hobbyNumber,userNumber);

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("favorites",favorites);
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(201, "생성 성공", responseMap));

    }

    //맞춤형 취미 추천 관련 모임 조회
    @GetMapping("/partyboards/search/{hobbyNumber}")
    public ResponseEntity<ResponseMessage> getPartyBoardByHobbyNumber(@PathVariable("hobbyNumber") int hobbyNumber) {

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        List<PartyBoardDTOForDetail> partyBoardDTOList = hobbyService.getPartyBoardByHobbyNumber(hobbyNumber);

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("party", partyBoardDTOList);
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "조회 성공", responseMap));
    }

    //키워드 검색후 취미 결과 조회 + 페이지네이션
    @GetMapping("/search-whole/hobby")
    public ResponseEntity<ResponseMessage> getHobbyByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size){

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();

        List<HobbyDTOForCard> hobbyCardDTOList = hobbyService.searchHobbyByKeyword(keyword, page, size);

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("hobbyCardDTOList", hobbyCardDTOList);
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "조회 성공", responseMap));
    }
}
