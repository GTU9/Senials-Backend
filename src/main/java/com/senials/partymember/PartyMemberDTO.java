package com.senials.partymember;

import com.senials.user.dto.UserDTOForPublic;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class PartyMemberDTO {

    private int partyMemberNumber;

    private UserDTOForPublic user;

    private int meetJoinedCnt;


    /* AllArgsConstructor */
    @Builder
    public PartyMemberDTO(int partyMemberNumber, UserDTOForPublic user, int meetJoinedCnt) {
        this.partyMemberNumber = partyMemberNumber;
        this.user = user;
        this.meetJoinedCnt = meetJoinedCnt;
    }
}
