package com.senials.report.controller;

import com.senials.common.ResponseMessage;
import com.senials.common.TokenParser;
import com.senials.config.HttpHeadersFactory;
import com.senials.report.dto.ReportDTO;
import com.senials.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReportController {

    private final TokenParser tokenParser;
    private final HttpHeadersFactory headersFactory;
    private final ReportService reportService;


    @Autowired
    public ReportController(
            TokenParser tokenParser
            , HttpHeadersFactory headersFactory
            , ReportService reportService
    ) {
        this.tokenParser = tokenParser;
        this.headersFactory = headersFactory;
        this.reportService = reportService;
    }


    /* 신고 전체 조회 (관리자만) */
    @GetMapping("/reports")
    public ResponseEntity<ResponseMessage> getReports (
            @RequestHeader(name = "Authorization") String token
            , @RequestParam int type
    ) {

        List<ReportDTO> reviewDTOList = reportService.getAllReportsByTargetType(type);

        Map<String, Object> responseMap = new HashMap<String, Object>();
        responseMap.put("reports", reviewDTOList);

        HttpHeaders headers = headersFactory.createJsonHeaders();
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "신고 목록 조회 성공", responseMap));
    }


    /* 신고하기 */
    @PostMapping("/reports")
    public ResponseEntity<ResponseMessage> postReport(
            @RequestHeader(name = "Authorization") String token
            , @RequestBody ReportDTO reportDTO
    ) {

        int userNumber = tokenParser.extractUserNumberFromToken(token);
        reportDTO.setReporterNumber(userNumber);

        reportService.registerReport(reportDTO);

        HttpHeaders headers = headersFactory.createJsonHeaders();
        return ResponseEntity.ok().headers(headers).body(new ResponseMessage(200, "신고 성공", null));
    }
}
