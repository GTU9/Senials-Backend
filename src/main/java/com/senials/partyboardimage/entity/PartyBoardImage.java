package com.senials.partyboardimage.entity;

import com.senials.partyboard.entity.PartyBoard;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "PARTY_BOARD_IMAGE")
public class PartyBoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_board_image_number")
    private int partyBoardImageNumber;

    // 모임 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_board_number", nullable = false)
    private PartyBoard partyBoard;

    // 이미지명
    @Column(name = "party_board_img", nullable = false, length = 255)
    private String partyBoardImg;

    /* AllArgsConstructor */
    @Builder
    public PartyBoardImage(int partyBoardImageNumber, PartyBoard partyBoard, String partyBoardImg) {
        this.partyBoardImageNumber = partyBoardImageNumber;
        this.partyBoard = partyBoard;
        this.partyBoardImg = partyBoardImg;
    }

}
