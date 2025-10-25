package com.senials.partyreview.entity;

import com.senials.partyboard.entity.PartyBoard;
import com.senials.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "PARTY_REVIEW")
public class PartyReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_review_number", nullable = false)
    private int partyReviewNumber; // 기본키, auto-increment

    // 작성자
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_number", referencedColumnName = "user_number", nullable = false)
    private User user; // 외래키 user_number -> User 엔티티와의 관계

    // 해당 모임
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_board_number", referencedColumnName = "party_board_number", nullable = false)
    private PartyBoard partyBoard; // 외래키 party_board_number -> PartyBoard 엔티티와의 관계

    // 별점
    @Column(name = "party_review_rate", nullable = false)
    private int partyReviewRate; // 1~5

    // 내용
    @Column(name = "party_review_detail", length = 5000)
    private String partyReviewDetail; // 리뷰 상세 내용

    // 작성일
    @Column(name = "party_review_write_date", nullable = false)
    private LocalDateTime partyReviewWriteDate; // 작성 날짜


    /* AllArgsConstructor */
    @Builder
    public PartyReview(int partyReviewNumber, User user, PartyBoard partyBoard, int partyReviewRate, String partyReviewDetail, LocalDateTime partyReviewWriteDate) {
        this.partyReviewNumber = partyReviewNumber;
        this.user = user;
        this.partyBoard = partyBoard;
        this.partyReviewRate = partyReviewRate;
        this.partyReviewDetail = partyReviewDetail;
        this.partyReviewWriteDate = partyReviewWriteDate;
    }


    /* 모임후기 작성 시 작성자, 모임, 작성일자 세팅 */
    public void initializeUser(User user) {
        this.user = user;
    }
    public void initializePartyBoard(PartyBoard partyBoard) {
        this.partyBoard = partyBoard;
    }
    public void initializePartyReviewWriteDate(LocalDateTime partyReviewWriteDate) {
        this.partyReviewWriteDate = partyReviewWriteDate;
    }


    /* 모임 후기 수정용 */
    public void updatePartyReviewRate(int partyReviewRate) {
        this.partyReviewRate = partyReviewRate;
    }
    public void updatePartyReviewDetail(String partyReviewDetail) {
        this.partyReviewDetail = partyReviewDetail;
    }

}
