package com.senials.meetmember.entity;

import com.senials.meet.entity.Meet;
import com.senials.partymember.entity.PartyMember;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "MEET_MEMBER")
public class MeetMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meet_member_number", nullable = false)
    private int meetMemberNumber; // auto-increment

    @ManyToOne
    @JoinColumn(name = "meet_number", referencedColumnName = "meet_number", nullable = false)
    private Meet meet; // 외래키 meet_number -> Meet 엔티티와의 관계

    @ManyToOne
    @JoinColumn(name = "party_member_number", referencedColumnName = "party_member_number", nullable = false)
    private PartyMember partyMember; // 외래키 party_member_number -> PartyMember 엔티티와의 관계


    /* AllArgsConstructor */
    public MeetMember(int meetMemberNumber, Meet meet, PartyMember partyMember) {
        this.meetMemberNumber = meetMemberNumber;
        this.meet = meet;
        this.partyMember = partyMember;
    }

}
