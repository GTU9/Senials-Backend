package com.senials.hobbyreview.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class HobbyReviewDTO {

    private int hobbyReviewNumber; // 취미 후기 고유 번호
    private int userNumber; // 사용자 번호
    private String userName; // 사용자 이름
    private int hobbyNumber; // 취미 고유 번호
    private int hobbyReviewRate; // 후기 평점 (1~5)
    private String hobbyReviewDetail; // 후기 상세 내용
    private int hobbyReviewHealthStatus; // 건강 상태 (예: true = 예, false = 아니요)
    private int hobbyReviewTendency; // 성향 (예: true = 외향적, false = 내향적)
    private int hobbyReviewLevel; // 취미 난이도 (1-쉬움, 2-약간 쉬움, 3-평범, 4-약간 어려움, 5-어려움)
    private int hobbyReviewBudget;
    private LocalDateTime hobbyReviewWriteDate; // 후기 작성 날짜

    public HobbyReviewDTO(int hobbyReviewNumber,
                          int userNumber,
                          String userName,
                          int hobbyNumber,
                          int hobbyReviewRate,
                          String hobbyReviewDetail,
                          int hobbyReviewHealthStatus,
                          int hobbyReviewTendency,
                          int hobbyReviewLevel,
                          int hobbyReviewBudget,
                          LocalDateTime hobbyReviewWriteDate) {
        this.hobbyReviewNumber = hobbyReviewNumber;
        this.userNumber = userNumber;
        this.userName = userName;
        this.hobbyNumber = hobbyNumber;
        this.hobbyReviewRate = hobbyReviewRate;
        this.hobbyReviewDetail = hobbyReviewDetail;
        this.hobbyReviewHealthStatus = hobbyReviewHealthStatus;
        this.hobbyReviewTendency = hobbyReviewTendency;
        this.hobbyReviewLevel = hobbyReviewLevel;
        this.hobbyReviewBudget=hobbyReviewBudget;
        this.hobbyReviewWriteDate = hobbyReviewWriteDate;
    }
}
