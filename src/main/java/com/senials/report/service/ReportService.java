package com.senials.report.service;

import com.senials.common.mapper.ReportMapper;
import com.senials.common.mapper.ReportMapperImpl;
import com.senials.hobbyreview.entity.HobbyReview;
import com.senials.hobbyreview.repository.HobbyReviewRepository;
import com.senials.partyboard.entity.PartyBoard;
import com.senials.partyboard.repository.PartyBoardRepository;
import com.senials.partyreview.entity.PartyReview;
import com.senials.partyreview.repository.PartyReviewRepository;
import com.senials.report.dto.ReportDTO;
import com.senials.report.entity.Report;
import com.senials.report.repository.ReportRepository;
import com.senials.user.entity.User;
import com.senials.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final ReportMapper reportMapper;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PartyBoardRepository partyBoardRepository;
    private final PartyReviewRepository partyReviewRepository;
    private final HobbyReviewRepository hobbyReviewRepository;


    public ReportService(
            ReportMapperImpl reportMapperImpl
            , ReportRepository reportRepository,
            UserRepository userRepository, PartyBoardRepository partyBoardRepository, PartyReviewRepository partyReviewRepository, HobbyReviewRepository hobbyReviewRepository) {
        this.reportMapper = reportMapperImpl;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.partyBoardRepository = partyBoardRepository;
        this.partyReviewRepository = partyReviewRepository;
        this.hobbyReviewRepository = hobbyReviewRepository;
    }

    /* 신고 대상 별 전체 신고 조회 */
    public List<ReportDTO> getAllReportsByTargetType(int type) {

        List<Report> reportList = reportRepository.findAllByReportTargetType(type, Sort.by("reportDate").descending());

        List<ReportDTO> reportDTOList = reportList.stream().map(report -> {
            ReportDTO reportDTO = reportMapper.toReportDTO(report);
            switch(type) {
                case 0:
                    reportDTO.setUserNumber(report.getUser().getUserNumber());
                    break;
                case 1:
                    reportDTO.setPartyBoardNumber(report.getPartyBoard().getPartyBoardNumber());
                    reportDTO.setPartyBoardName(report.getPartyBoard().getPartyBoardName());
                    break;
                case 2:
                    reportDTO.setPartyReviewNumber(report.getPartyReview().getPartyReviewNumber());
                    reportDTO.setPartyReviewDetail(report.getPartyReview().getPartyReviewDetail());
                    reportDTO.setPartyBoardNumber(report.getPartyReview().getPartyBoard().getPartyBoardNumber());
                    break;
                case 3:
                    reportDTO.setHobbyReviewNumber(report.getHobbyReview().getHobbyReviewNumber());
                    reportDTO.setHobbyReviewDetail(report.getHobbyReview().getHobbyReviewDetail());
                    reportDTO.setHobbyNumber(report.getHobbyReview().getHobby().getHobbyNumber());
                    break;
                default:
                    throw new IllegalArgumentException("잘못된 요청입니다.");
            }
            return reportDTO;

        }).toList();

        return reportDTOList;
    }


    @Transactional
    public void registerReport(ReportDTO reportDTO) {

        User reporter = userRepository.findById(reportDTO.getReporterNumber())
                .orElseThrow(IllegalArgumentException::new);

        Report newReport = reportMapper.toReport(reportDTO);
        newReport.initializeReporter(reporter);
        newReport.initializeReportDate(LocalDateTime.now());

        int targetType = reportDTO.getReportTargetType();
        int targetNumber = reportDTO.getReportTargetNumber();
        switch (targetType) {
            case 0:
                User user = userRepository.findById(targetNumber)
                        .orElseThrow(IllegalArgumentException::new);
                user.increaseReportCnt();
                newReport.initializeUser(user);
                break;
            case 1:
                PartyBoard partyBoard = partyBoardRepository.findById(targetNumber)
                        .orElseThrow(IllegalArgumentException::new);
                newReport.initializePartyBoard(partyBoard);
                break;
            case 2:
                PartyReview partyReview = partyReviewRepository.findById(targetNumber)
                        .orElseThrow(IllegalArgumentException::new);
                newReport.initializePartyReview(partyReview);
                break;
            case 3:
                HobbyReview hobbyReview = hobbyReviewRepository.findById(targetNumber)
                        .orElseThrow(IllegalArgumentException::new);
                newReport.initializeHobbyReview(hobbyReview);
                break;
            default:
                throw new IllegalArgumentException("잘못된 요청입니다.");
        }

        reportRepository.save(newReport);
    }
}
