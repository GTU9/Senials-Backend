package com.senials.hobbyboard.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class HobbyDTOForCard {

    private int hobbyNumber;       // 취미 번호
    private String hobbyName;      // 취미 이름
    private String hobbyImg;       // 취미 이미지 경로

    public HobbyDTOForCard(int hobbyNumber, String hobbyName, String hobbyImg) {
        this.hobbyNumber = hobbyNumber;
        this.hobbyName = hobbyName;
        this.hobbyImg = hobbyImg;
    }
}
