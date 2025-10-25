package com.senials.hobbyreview.entity;

import com.senials.hobbyboard.entity.Hobby;
import com.senials.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "HOBBY_REVIEW")
public class HobbyReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hobby_review_number", nullable = false)
    private int hobbyReviewNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_number", referencedColumnName = "user_number", nullable = false)
    private User user; // 외래키 user_number -> User 엔티티

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hobby_number", referencedColumnName = "hobby_number", nullable = false)
    private Hobby hobby; // 외래키 hobby_number -> Hobby 엔티티

    @Column(name = "hobby_review_rate", nullable = false)
    private int hobbyReviewRate; // 1~5 (리뷰 평점)

    @Column(name = "hobby_review_detail", length = 5000)
    private String hobbyReviewDetail; // 리뷰 상세 내용

    @Column(name = "hobby_review_health_status", nullable = false)
    private int hobbyReviewHealthStatus; // 건강 상태 (1=예, 0=아니요)

    @Column(name = "hobby_review_tendency", nullable = false)
    private int hobbyReviewTendency; // 성향 (1=외향적, 0=내향적)

    @Column(name = "hobby_review_level", nullable = false)
    private int hobbyReviewLevel; // 난이도 (0-쉬움, 1-약간쉬움, 2-평범, 3-약간어려움, 4-어려움)

    @Column(name = "hobby_review_budget", nullable = false)
    private int hobbyReviewBudget; // 비용 (0- 0~100,000, 1- 100,000~400,000-, 2- 400,000~1,000,000, 3- 1,000,000~)

    @Column(name = "hobby_review_write_date", nullable = false)
    private LocalDateTime hobbyReviewWriteDate; // 리뷰 작성 날짜


    /* AllArgsConstructor */
    @Builder
    public HobbyReview(int hobbyReviewNumber,
                       User user,
                       Hobby hobby,
                       int hobbyReviewRate,
                       String hobbyReviewDetail,
                       int hobbyReviewHealthStatus,
                       int hobbyReviewTendency, int hobbyReviewLevel,
                       int hobbyReviewBudget,
                       LocalDateTime hobbyReviewWriteDate) {
        this.hobbyReviewNumber = hobbyReviewNumber;
        this.user = user;
        this.hobby = hobby;
        this.hobbyReviewRate = hobbyReviewRate;
        this.hobbyReviewDetail = hobbyReviewDetail;
        this.hobbyReviewHealthStatus = hobbyReviewHealthStatus;
        this.hobbyReviewTendency = hobbyReviewTendency;
        this.hobbyReviewLevel = hobbyReviewLevel;
        this.hobbyReviewBudget=hobbyReviewBudget;
        this.hobbyReviewWriteDate = hobbyReviewWriteDate;
    }

    public void InitializeHobby(Hobby hobby) {
        this.hobby = hobby;
    }

    public void InitializeUser(User user){
        this.user=user;
    }

    public void updateHobbyReviewRate(int hobbyReviewRate) {
        this.hobbyReviewRate = hobbyReviewRate;
    }

    public void updateHobbyReviewDetail(String hobbyReviewDetail) {
        this.hobbyReviewDetail = hobbyReviewDetail;
    }

    public void updateHobbyReviewHealthStatus(int hobbyReviewHealthStatus) {
        this.hobbyReviewHealthStatus = hobbyReviewHealthStatus;
    }

    public void updateHobbyReviewTendency(int hobbyReviewTendency) {
        this.hobbyReviewTendency = hobbyReviewTendency;
    }

    public void updateHobbyReviewLevel(int hobbyReviewLevel) {
        this.hobbyReviewLevel = hobbyReviewLevel;
    }
    public void updateHobbyReviewBudget(int hobbyReviewBudget) {
        this.hobbyReviewBudget = hobbyReviewBudget;
    }

}
