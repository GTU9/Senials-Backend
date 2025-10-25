package com.senials.partymember.entity;

import com.senials.meetmember.entity.MeetMember;
import com.senials.partyboard.entity.PartyBoard;
import com.senials.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "PARTY_MEMBER")
public class PartyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_member_number", nullable = false)
    private int partyMemberNumber;

    @ManyToOne
    @JoinColumn(name = "party_board_number", nullable = false)
    private PartyBoard partyBoard;

    @ManyToOne
    @JoinColumn(name = "user_number", nullable = false)
    private User user;


    /* 관계 설정 */
    // 참여한 일정
    @OneToMany(mappedBy = "partyMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeetMember> meetMembershipList;


    /* AllArgsConstructor */
    public PartyMember(int partyMemberNumber, PartyBoard partyBoard, User user) {
        this.partyMemberNumber = partyMemberNumber;
        this.partyBoard = partyBoard;
        this.user = user;
    }

}
