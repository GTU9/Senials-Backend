package com.senials.partyboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class PartyBoardDTOForModify {

    private int partyBoardNumber;

    private int hobbyNumber;

    private String partyBoardName;

    private String partyBoardDetail;

    private int partyBoardStatus;

    /* AllArgsConstructor */
    public PartyBoardDTOForModify(int partyBoardNumber, int hobbyNumber, String partyBoardName, String partyBoardDetail, int partyBoardStatus) {
        this.partyBoardNumber = partyBoardNumber;
        this.hobbyNumber = hobbyNumber;
        this.partyBoardName = partyBoardName;
        this.partyBoardDetail = partyBoardDetail;
        this.partyBoardStatus = partyBoardStatus;
    }
}
