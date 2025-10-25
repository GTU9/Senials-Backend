package com.senials.report.entity;

import com.senials.hobbyreview.entity.HobbyReview;
import com.senials.partyboard.entity.PartyBoard;
import com.senials.partyreview.entity.PartyReview;
import com.senials.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@NoArgsConstructor
@Getter
@ToString
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_number")
    private int reportNumber;

    // 신고자
    @ManyToOne
    @JoinColumn(name = "reporter_number", nullable = false)
    private User reporter;

    // 피신고자
    @ManyToOne
    @JoinColumn(name = "user_number")
    private User user;

    // 피신고 모임
    @ManyToOne
    @JoinColumn(name = "party_board_number")
    private PartyBoard partyBoard;

    // 피신고 모임후기
    @ManyToOne
    @JoinColumn(name = "party_review_number")
    private PartyReview partyReview;

    // 피신고 취미후기
    @ManyToOne
    @JoinColumn(name = "hooby_review_number")
    private HobbyReview hobbyReview;

    @Column(name = "report_type", nullable = false)
    private int reportType;

    @Column(name = "report_detail", nullable = false, length = 5000)
    private String reportDetail;

    @Column(name = "report_target_type", nullable = false)
    private int reportTargetType;

    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;


    /* AllArgsConstructor */
    @Builder
    public Report(int reportNumber, User reporter, User user, PartyBoard partyBoard, PartyReview partyReview, HobbyReview hobbyReview, int reportType, String reportDetail, int reportTargetType, LocalDateTime reportDate) {
        this.reportNumber = reportNumber;
        this.reporter = reporter;
        this.user = user;
        this.partyBoard = partyBoard;
        this.partyReview = partyReview;
        this.hobbyReview = hobbyReview;
        this.reportType = reportType;
        this.reportDetail = reportDetail;
        this.reportTargetType = reportTargetType;
        this.reportDate = reportDate;
    }


    /* 초기 설정용 */
    public void initializeReporter(User user){
        this.reporter = user;
    }

    public void initializeUser(User user) {
        this.user = user;
    }

    public void initializePartyBoard(PartyBoard partyBoard) {
        this.partyBoard = partyBoard;
    }

    public void initializePartyReview(PartyReview partyReview) {
        this.partyReview = partyReview;
    }

    public void initializeHobbyReview(HobbyReview hobbyReview) {
        this.hobbyReview = hobbyReview;
    }

    public void initializeReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }
}
