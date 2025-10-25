package com.senials.partyreview.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class PartyReviewDTO {

    private int partyReviewNumber;

    private int userNumber;

    private int partyBoardNumber;

    private int partyReviewRate;

    private String partyReviewDetail;

    private LocalDateTime partyReviewWriteDate;

    /* AllArgsConstructor */
    public PartyReviewDTO(int partyReviewNumber, int userNumber, int partyBoardNumber, int partyReviewRate, String partyReviewDetail, LocalDateTime partyReviewWriteDate) {
        this.partyReviewNumber = partyReviewNumber;
        this.userNumber = userNumber;
        this.partyBoardNumber = partyBoardNumber;
        this.partyReviewRate = partyReviewRate;
        this.partyReviewDetail = partyReviewDetail;
        this.partyReviewWriteDate = partyReviewWriteDate;
    }
}
