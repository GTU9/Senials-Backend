package com.senials.partyboardimage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class PartyBoardImageDTO {

    private int partyBoardImageNumber;

    private Integer partyBoardNumber;

    private String partyBoardImg;

    /* AllArgsConstructor */
    public PartyBoardImageDTO(int partyBoardImageNumber, Integer partyBoardNumber, String partyBoardImg) {
        this.partyBoardImageNumber = partyBoardImageNumber;
        this.partyBoardNumber = partyBoardNumber;
        this.partyBoardImg = partyBoardImg;
    }
}
