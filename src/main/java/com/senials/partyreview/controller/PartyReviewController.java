package com.senials.partyreview.controller;

import com.senials.common.ResponseMessage;
import com.senials.common.TokenParser;
import com.senials.config.HttpHeadersFactory;
import com.senials.partyreview.dto.PartyReviewDTO;
import com.senials.partyreview.dto.PartyReviewDTOForDetail;
import com.senials.partyreview.entity.PartyReview;
import com.senials.partyreview.service.PartyReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class PartyReviewController {

    private final HttpHeadersFactory httpHeadersFactory;
    private final TokenParser tokenParser;
    private final PartyReviewService partyReviewService;

    public PartyReviewController(
            HttpHeadersFactory httpHeadersFactory
            , TokenParser tokenParser
            , PartyReviewService partyReviewService
    )
    {
        this.httpHeadersFactory = httpHeadersFactory;
        this.tokenParser = tokenParser;
        this.partyReviewService = partyReviewService;
    }


    /* 모임 후기 단일 조회 */
    @GetMapping("/partyreviews/{partyReviewNumber}")
    public ResponseEntity<ResponseMessage> getPartyReview(
            @PathVariable int partyReviewNumber
    ) {
        System.out.println(partyReviewNumber);
        PartyReviewDTO partyReview = partyReviewService.getPartyReview(partyReviewNumber);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("partyReview", partyReview);

        HttpHeaders headers = httpHeadersFactory.createJsonHeaders();
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "모임 후기 조회 성공", responseMap));
    }



    /* 특정 모임 후기 단일 조회 */
    @GetMapping("/partyboards/{partyBoardNumber}/partyreviews/{partyReviewNumber}")
    public ResponseEntity<ResponseMessage> getMyPartyReview(
            @PathVariable Integer partyBoardNumber
            , @PathVariable Integer partyReviewNumber
            , @RequestHeader(name = "Authorization") String token
    ) {
        int userNumber = tokenParser.extractUserNumberFromToken(token);


        PartyReviewDTOForDetail partyReviewDTO = partyReviewService.getOnePartyReview(userNumber, partyBoardNumber, partyReviewNumber);


        if (partyReviewDTO == null) {
            return ResponseEntity.status(404)
                    .body(new ResponseMessage(404, "작성한 모임 후기를 찾을 수 없습니다.", null));
        }


        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("startDate", partyReviewDTO.getPartyReviewWriteDate().toLocalDate()); // 시작 날짜
        responseMap.put("startTime", partyReviewDTO.getPartyReviewWriteDate().toLocalTime()); // 시작 시간
        responseMap.put("rating", partyReviewDTO.getPartyReviewRate()); // 평점
        responseMap.put("content", partyReviewDTO.getPartyReviewDetail()); // 내용

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));

        return ResponseEntity.ok().headers(headers)
                .body(new ResponseMessage(200, "작성한 모임 후기 조회 성공", responseMap));
    }



    /* 모임 후기 전체 조회*/
    @GetMapping("/partyboards/{partyBoardNumber}/partyreviews")
    public ResponseEntity<ResponseMessage> getPartyReviewsByPartyBoardNumber(
            @PathVariable Integer partyBoardNumber
            , @RequestParam(required = false, defaultValue = "4") Integer pageSize
            , @RequestParam(required = false, defaultValue = "0") Integer pageNumber
    ) {

        int partyReviewCnt = partyReviewService.countPartyReviews(partyBoardNumber);
        double partyAvgReviewRate = partyReviewService.getAverageReviewRate(partyBoardNumber);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("partyReviewWriteDate").descending());
        List<PartyReviewDTOForDetail> partyReviewDTOList = partyReviewService.getPartyReviews(partyBoardNumber, pageable);

        boolean hasMoreReviews = (pageNumber + 1) * pageSize < partyReviewCnt;

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("partyReviewCnt", partyReviewCnt);
        responseMap.put("partyAvgReviewRate", partyAvgReviewRate);
        responseMap.put("partyReviews", partyReviewDTOList);
        responseMap.put("hasMoreReviews", hasMoreReviews);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "모임 후기 전체 조회 성공", responseMap));
    }

    /* 모임 후기 작성 */
    @PostMapping("/partyboards/{partyBoardNumber}/partyreviews")
    public ResponseEntity<ResponseMessage> registerPartyReview (
            @PathVariable Integer partyBoardNumber
            , @RequestHeader(name = "Authorization") String token
            , @RequestBody PartyReviewDTO partyReviewDTO
    ) {
        int userNumber = tokenParser.extractUserNumberFromToken(token);
        System.out.println("$$$$$$$$$$$$$$$$$$$" + partyReviewDTO);

        partyReviewService.registerPartyReview(userNumber, partyBoardNumber, partyReviewDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "모임 후기 작성 성공", null));
    }


    /* 모임 후기 수정 */
    @PutMapping("/partyboards/{partyBoardNumber}/partyreviews/{partyReviewNumber}")
    public ResponseEntity<ResponseMessage> modifyPartyReview (
            @PathVariable Integer partyReviewNumber
            , @RequestBody PartyReviewDTO partyReviewDTO
    ) {

        partyReviewService.modifyPartyReview(partyReviewNumber, partyReviewDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "모임 후기 수정 성공", null));
    }

    /* 모임 후기 삭제 */
    @DeleteMapping("/partyboards/{partyBoardNumber}/partyreviews/{partyReviewNumber}")
    public ResponseEntity<ResponseMessage> removePartyReview (
            @PathVariable Integer partyReviewNumber
    ) {

        partyReviewService.removePartyReview(partyReviewNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "모임 후기 삭제 성공", null));
    }

}
