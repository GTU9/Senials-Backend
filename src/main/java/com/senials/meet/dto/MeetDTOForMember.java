package com.senials.meet.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class MeetDTOForMember {

    private int meetNumber;

    private int partyBoardNumber;

    private LocalDate meetStartDate;

    private LocalDate meetEndDate;

    private LocalTime meetStartTime;

    private LocalTime meetFinishTime;

    private int meetEntryFee;

    private String meetLocation;

    private int meetMaxMember;


    /* AllArgsConstructor */
    public MeetDTOForMember(int meetNumber, int partyBoardNumber, LocalDate meetStartDate, LocalDate meetEndDate, LocalTime meetStartTime, LocalTime meetFinishTime, int meetEntryFee, String meetLocation, int meetMaxMember) {
        this.meetNumber = meetNumber;
        this.partyBoardNumber = partyBoardNumber;
        this.meetStartDate = meetStartDate;
        this.meetEndDate = meetEndDate;
        this.meetStartTime = meetStartTime;
        this.meetFinishTime = meetFinishTime;
        this.meetEntryFee = meetEntryFee;
        this.meetLocation = meetLocation;
        this.meetMaxMember = meetMaxMember;
    }
}
