package com.senials.partyboard.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class PartyBoardDTOForWrite {

    private int hobbyNumber;

    private String partyBoardName;

    private String partyBoardDetail;

    public PartyBoardDTOForWrite(int hobbyNumber, String partyBoardName, String partyBoardDetail) {
        this.hobbyNumber = hobbyNumber;
        this.partyBoardName = partyBoardName;
        this.partyBoardDetail = partyBoardDetail;
    }
}
