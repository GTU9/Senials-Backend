package com.senials.report.dto;

import com.senials.user.dto.UserCommonDTO;
import com.senials.user.dto.UserDTO;
import com.senials.user.dto.UserDTOForPublic;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReportDTO {

    private int reportNumber;

    private int reporterNumber;
    private UserDTOForPublic reporter;

    private int userNumber;
    private UserDTOForPublic user;

    private int partyBoardNumber;
    private String partyBoardName;

    private int partyReviewNumber;
    private String partyReviewDetail;

    private int hobbyReviewNumber;
    private int hobbyNumber;
    private String hobbyReviewDetail;

    private int reportType;

    private String reportDetail;

    private int reportTargetType;

    private int reportTargetNumber;

    private LocalDateTime reportDate;


    /* AllArgsConstructor */
    @Builder
    public ReportDTO(int reportNumber, int reporterNumber, UserDTOForPublic reporter, int userNumber, UserDTOForPublic user, int partyBoardNumber, String partyBoardName, int partyReviewNumber, String partyReviewDetail, int hobbyReviewNumber, String hobbyReviewDetail, int reportType, String reportDetail, int reportTargetType, int reportTargetNumber, LocalDateTime reportDate) {
        this.reportNumber = reportNumber;
        this.reporterNumber = reporterNumber;
        this.reporter = reporter;
        this.userNumber = userNumber;
        this.user = user;
        this.partyBoardNumber = partyBoardNumber;
        this.partyBoardName = partyBoardName;
        this.partyReviewNumber = partyReviewNumber;
        this.partyReviewDetail = partyReviewDetail;
        this.hobbyReviewNumber = hobbyReviewNumber;
        this.hobbyReviewDetail = hobbyReviewDetail;
        this.reportType = reportType;
        this.reportDetail = reportDetail;
        this.reportTargetType = reportTargetType;
        this.reportTargetNumber = reportTargetNumber;
        this.reportDate = reportDate;
    }
}
