package com.senials.meet.MeetDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@Setter
public class MeetDTO {
    private int meetNumber;
    private LocalDate meetStartDate;
    private LocalDate meetEndDate;
    private LocalTime meetStartTime;
    private LocalTime meetFinishTime;
    private int meetEntryFee;
    private String meetLocation;
    private int meetMaxMember;

    public MeetDTO(int meetNumber, LocalDate meetStartDate, LocalDate meetEndDate, LocalTime meetStartTime,
                   LocalTime meetFinishTime, int meetEntryFee, String meetLocation, int meetMaxMember) {
        this.meetNumber = meetNumber;
        this.meetStartDate = meetStartDate;
        this.meetEndDate = meetEndDate;
        this.meetStartTime = meetStartTime;
        this.meetFinishTime = meetFinishTime;
        this.meetEntryFee = meetEntryFee;
        this.meetLocation = meetLocation;
        this.meetMaxMember = meetMaxMember;
    }
}
